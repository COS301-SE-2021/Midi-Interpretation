package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidInterpretationException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.broker.InterpreterBroker;
import com.noxception.midisense.interpreter.broker.JSONUtils;
import com.noxception.midisense.interpreter.broker.RequestMonitor;
import com.noxception.midisense.interpreter.parser.Score;
import com.noxception.midisense.interpreter.repository.DatabaseManager;
import com.noxception.midisense.interpreter.repository.ScoreEntity;
import com.noxception.midisense.interpreter.repository.ScoreRepository;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * Class that is used for uploading a midi file to a specified storage location temporarily.
 * In addition it allows the interpretation of works whose corresponding metadata can be persisted to an external CRUD repository by way of a JPA extension
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
@Service
public class InterpreterServiceImpl implements InterpreterService{

    private final DatabaseManager databaseManager;
    private final StandardConfig configurations;
    private final RequestMonitor<Score> requestMonitor;

    @Autowired
    public InterpreterServiceImpl(ScoreRepository scoreRepository, MIDISenseConfig midiSenseConfig) {
        this.databaseManager = new DatabaseManager();
        this.databaseManager.attachRepository(scoreRepository);
        this.configurations = midiSenseConfig;
        this.requestMonitor = new RequestMonitor<>();
    }

    public InterpreterServiceImpl(DatabaseManager databaseManager, StandardConfig standardConfig){
        this.configurations = standardConfig;
        this.databaseManager = databaseManager;
        this.requestMonitor = new RequestMonitor<>();
    }

    //=================================
    // MAIN SERVICE USE CASES
    //=================================

    /**Handles the uploading of an incoming MIDI file saved to a temporary storage location
     *
     * @param request an object encapsulating the file to be uploaded
     * @return an object encapsulating the success of the upload by means of a valid identifier generated to refer to the file
     * @throws InvalidUploadException if any one of the following criterion are met:
     * <ul>
     *     <li>The file is empty</li>
     *     <li>The file exceeds the maximum upload size</li>
     *     <li>The file storage system is unable to process the file</li>
     * </ul>
     */
    @Transactional
    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) throws InvalidUploadException {

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidUploadException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        try {
            //get the contents of the file
            byte[] fileContents = request.getFileContents();

            final int maximumUploadSize = kilobytesToBytes(Integer.parseInt(configurations.configuration(ConfigurationName.MAX_FILE_UPLOAD_SIZE)));
            //check to see that it isn't empty, or is of maximum length
            if (fileContents.length==0)
                throw new InvalidUploadException(configurations.configuration(ConfigurationName.EMPTY_FILE_EXCEPTION_TEXT));

            else if (fileContents.length > maximumUploadSize)
                throw new InvalidUploadException(configurations.configuration(ConfigurationName.FILE_TOO_LARGE_EXCEPTION_TEXT));

            //write the file to temporary storage
            UUID fileDesignator = writeFileToStorage(fileContents);

            return new UploadFileResponse(fileDesignator);

        }
        catch (IOException e) {
            //If the file cannot be written to by the file system, then the method should be deemed to have failed
            throw new InvalidUploadException(configurations.configuration(ConfigurationName.FILE_SYSTEM_EXCEPTION_TEXT));
        }
    }

    /**Handles the interpretation of a MIDI file saved to temporary storage, initiates parsing of the work and persists
     * the interpreted work to an external database
     *
     * @param request an object encapsulating a reference to the file uploaded in temporary storage
     * @return an object encapsulating the successfulness of interpretation, along with a descriptive message
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in file storage
     */
    @Transactional
    @Override
    public ProcessFileResponse processFile(ProcessFileRequest request) throws InvalidDesignatorException{

        if(request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        try {

            //parse file to rich format and get the corresponding score
            UUID fileDesignator = request.getFileDesignator();

            //if the file already exists, then throw error
            if(scoreExists(fileDesignator))
                throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_ALREADY_EXISTS_EXCEPTION_TEXT));

            ParseJSONResponse parserResponse = this.parseJSON(new ParseJSONRequest(fileDesignator));
            Score parsedScore = parserResponse.getParsedScore();

            //get the designator for the file being stored temporarily
            String filename = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+configurations.configuration(ConfigurationName.FILE_FORMAT);
            byte[] fileData = Files.readAllBytes(Paths.get(filename));

            //persist score details to database
            saveScore(parsedScore,fileDesignator,fileData);

            return new ProcessFileResponse(true,configurations.configuration(ConfigurationName.SUCCESSFUL_PARSING_TEXT));
        }
        catch(InvalidInterpretationException m){
            //The file is not a valid midi format and interpretation fails
            return new ProcessFileResponse(false, configurations.configuration(ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));
        }
        catch (IOException e) {
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));
        }

    }

    //=================================
    // AUX SERVICE USE CASES
    //=================================

    /**Produces a complete tree-based representation of a midi file and its track sequences stored in temporary storage.
     *
     * @param request an object encapsulating a reference to the file uploaded in temporary storage
     * @return an object encapsulating the tempo indication of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidInterpretationException {

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));
        try {

            //get the designator for the file being stored temporarily
            UUID fileDesignator = request.getFileDesignator();
            String filename = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+configurations.configuration(ConfigurationName.FILE_FORMAT);
            byte[] fileData = Files.readAllBytes(Paths.get(filename));


            int[] brokerData = new int[fileData.length];
            for(int i=0; i<fileData.length; i++){
                brokerData[i] = Byte.toUnsignedInt(fileData[i]);
            }

            requestMonitor.monitor();
            new InterpreterBroker(this.configurations).makeRequest(
                    brokerData,
                    (res)-> {
                        try {
                            Score score = JSONUtils.JSONToObject(res.body(), Score.class);
                            this.requestMonitor.acquire(score);
                        } catch (IOException e) {
                            this.requestMonitor.abort();
                        }
                    },
                    (res)-> {
                        this.requestMonitor.abort();
                    }
            );
            requestMonitor.await();
            Score returnScore = requestMonitor.getResource();
            if(returnScore == null)
                throw new InvalidInterpretationException(configurations.configuration(ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));

            return new ParseJSONResponse(returnScore);
        }
        catch (IOException e) {
            //If the file doesn't exist or cannot be written to by the file system, then the method should be deemed to have failed
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));
        }
        catch (ExecutionException e) {
            //The file is not a valid midi format and interpretation fails
            throw new InvalidInterpretationException(configurations.configuration(ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));
        } catch (CancellationException | CompletionException | InterruptedException t) {
            throw new InvalidInterpretationException(configurations.configuration(ConfigurationName.INVALID_MIDI_TIMEOUT_EXCEPTION_TEXT));
        }
    }

    //================================
    // HELPER METHODS
    //================================

    /** A converter between kilobyte and byte measure
     *
     * @param n the number of kilobytes
     * @return the number of bytes
     */
    private static int kilobytesToBytes(int n){
        return (int) (n*Math.pow(2,10));
    }

    /** writes a file, in the form of a byte stream, to temporary storage
     *
     * @param fileContents a byte string representing the contents of a file
     * @return a uniquely generated value corresponding to the name of the stored file
     * @throws IOException if the OS file system produces an error while writing to the file
     */
    private UUID writeFileToStorage(byte[] fileContents) throws IOException{

        //generate unique fileDesignator
        UUID fileDesignator = UUID.randomUUID();

        //create target file and write to it
        String fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+configurations.configuration(ConfigurationName.FILE_FORMAT);
        FileOutputStream os = new FileOutputStream(fileName);
        os.write(fileContents);
        os.close();

        return fileDesignator;
    }

    /** deletes a file from temporary storage
     *
     * @param fileDesignator the unique reference to a stored file
     * @throws IOException if the OS file system cannot delete the file, due to:
     * <ul>
     *     <li>The file not existing</li>
     *     <li>The OS File System throwing an error </li>
     * </ul>
     */
    private void deleteFileFromStorage(UUID fileDesignator) throws IOException{
        File file = new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+configurations.configuration(ConfigurationName.FILE_FORMAT));
        if (!file.delete())
            throw new IOException("Unable to delete file "+file.getName());
    }


    /** Persists a score object to an external repository. See {@link ScoreRepository}
     *
     * @param score encapsulates the interpreted midi file
     * @param fileDesignator the unique identifier corresponding to the file in temporary storage
     */
    @Transactional
    public void saveScore(Score score, UUID fileDesignator, byte[] fileContents) throws IOException {

        //create a score
        ScoreEntity scoreEntity = new ScoreEntity(score,fileDesignator,fileContents);
        databaseManager.save(scoreEntity);

        //delete from storage
        deleteFileFromStorage(fileDesignator);
    }

    /** Method to see if a designator already corresponds to an interpreted file
     *
     * @param fileDesignator the designator of the work
     * @return the existence and uniqueness of the interpreted work
     */
    public boolean scoreExists(UUID fileDesignator){
        Optional<ScoreEntity> searchResults = databaseManager.findByFileDesignator(fileDesignator.toString());
        return searchResults.isPresent();
    }


}
