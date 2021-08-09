package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.MIDISenseParserListener;
import com.noxception.midisense.interpreter.parser.Score;
import com.noxception.midisense.interpreter.parser.Track;
import com.noxception.midisense.interpreter.repository.ScoreEntity;
import com.noxception.midisense.interpreter.repository.ScoreRepository;
import com.noxception.midisense.interpreter.repository.TrackEntity;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.midi.InvalidMidiDataException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that is used for uploading a midi file to a specified storage location temporarily.
 * In addition it allows the interpretation of works whose corresponding metadata can be persisted to an external CRUD repository by way of a JPA extension
 *
 * More details on parsing can be found at {@link MIDISenseParserListener}
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


    @Autowired
    private ScoreRepository scoreRepository;

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
            throw new InvalidUploadException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        try {
            //get the contents of the file
            byte[] fileContents = request.getFileContents();

            final int maximumUploadSize = kilobytesToBytes(Integer.parseInt(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MAX_FILE_UPLOAD_SIZE)));
            //check to see that it isn't empty, or is of maximum length
            if (fileContents.length==0)
                throw new InvalidUploadException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_FILE_EXCEPTION_TEXT));

            else if (fileContents.length > maximumUploadSize)
                throw new InvalidUploadException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_TOO_LARGE_EXCEPTION_TEXT));

            //write the file to temporary storage
            UUID fileDesignator = writeFileToStorage(fileContents);

            return new UploadFileResponse(fileDesignator);

        }
        catch (IOException e) {
            //If the file cannot be written to by the file system, then the method should be deemed to have failed
            throw new InvalidUploadException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_SYSTEM_EXCEPTION_TEXT));
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
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        try {
            //parse file to rich format and get the corresponding score
            UUID fileDesignator = request.getFileDesignator();

            //if the file already exists, then throw error
            if(scoreExists(fileDesignator)) throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_ALREADY_EXISTS_EXCEPTION_TEXT));

            ParseJSONResponse parserResponse = this.parseJSON(new ParseJSONRequest(fileDesignator));
            Score parsedScore = parserResponse.getParsedScore();

            //persist score details to database
            saveScore(parsedScore,fileDesignator);

            return new ProcessFileResponse(true,MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.SUCCESSFUL_PARSING_TEXT));
        }
        catch(InvalidMidiDataException m){
            //The file is not a valid midi format and interpretation fails
            return new ProcessFileResponse(false, MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));
        }

    }

    //=================================
    // AUX SERVICE USE CASES
    //=================================

    /**Handles the retrieval of a midi file's metre (time signature and beat encoding)
     *
     * @param request an object encapsulating a reference to the file uploaded in persisted storage
     * @return an object encapsulating the metre of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public InterpretMetreResponse interpretMetre(InterpretMetreRequest request) throws InvalidDesignatorException {

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());

        if (searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //get the persisted data
        String metre = searchResults.get().getTimeSignature();
        int numBeats = Integer.parseInt(metre.substring(0, metre.indexOf("/")));
        int beatValue = Integer.parseInt(metre.substring(metre.indexOf("/")+1));

        return new InterpretMetreResponse(numBeats,beatValue);
    }

    /**Handles the retrieval of a midi file's tempo indication
     *
     * @param request an object encapsulating a reference to the file uploaded in persisted storage
     * @return an object encapsulating the tempo indication of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public InterpretTempoResponse interpretTempo(InterpretTempoRequest request) throws InvalidDesignatorException {

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());

        if(searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //get the persisted data
        int tempoIndication = searchResults.get().getTempoIndication();

        return new InterpretTempoResponse(tempoIndication);
    }

    /**Handles the retrieval of a midi file's key signature
     *
     * @param request an object encapsulating a reference to the file uploaded in persisted storage
     * @return an object encapsulating the key signature of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public InterpretKeySignatureResponse interpretKeySignature(InterpretKeySignatureRequest request) throws InvalidDesignatorException {

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());

        if(searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //get the persisted data
        String sigName = searchResults.get().getKeySignature();

        return new InterpretKeySignatureResponse(sigName);
    }

    /**Produces a human-readable string representation of a midi file stored in temporary storage
     *
     * @param request an object encapsulating a reference to the file uploaded in temporary storage
     * @return an object encapsulating the string representation of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Deprecated
    @Override
    public ParseStaccatoResponse parseStaccato(ParseStaccatoRequest request) throws InvalidDesignatorException{

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));
        try {
            //get the designator for the file being stored temporarily
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT));
            Pattern pattern  = MidiFileManager.loadPatternFromMidi(sourceFile);
            return new ParseStaccatoResponse(pattern.toString());
        }
        catch (IOException e) {
            //If the file doesn't exist or cannot be written to by the file system, then the method should be deemed to have failed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));
        }
        catch (InvalidMidiDataException e) {
            //The file is not a valid midi format and interpretation fails
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));
        }
    }

    /**Produces a complete tree-based representation of a midi file and its track sequences stored in temporary storage.
     * Relies upon the jFugue 5.0.9 Midi Parser, with the NoXception Midi Parser Listener
     *
     * For more details on the parsing mechanism, see {@link MIDISenseParserListener}
     *
     * @param request an object encapsulating a reference to the file uploaded in temporary storage
     * @return an object encapsulating the tempo indication of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidMidiDataException {

        if (request==null)
            //an empty request cannot be processed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));
        try {

            //get the designator for the file being stored temporarily
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT));

            //create a parser and corresponding listener
            MidiParser parser = new MidiParser();
            MIDISenseParserListener listener = new MIDISenseParserListener();
            parser.addParserListener(listener);

            //start parsing
            parser.parse(MidiFileManager.load(sourceFile));

            return new ParseJSONResponse(listener.getParsedScore());
        }
        catch (IOException e) {
            //If the file doesn't exist or cannot be written to by the file system, then the method should be deemed to have failed
            throw new InvalidDesignatorException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));
        }
        catch (InvalidMidiDataException e) {
            //The file is not a valid midi format and interpretation fails
            throw new InvalidMidiDataException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));
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
        String fileName = MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);
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
        File file = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignator+MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT));
        if (!file.delete())
            throw new IOException("Unable to delete file "+file.getName());
    }


    /** Persists a score object to an external repository. See {@link ScoreRepository}
     *
     * @param score encapsulates the interpreted midi file
     * @param fileDesignator the unique identifier corresponding to the file in temporary storage
     * @return whether or not the score is successfully persisted
     */
    @Transactional
    public boolean saveScore(Score score, UUID fileDesignator){

        //create a score
        ScoreEntity scoreEntity = new ScoreEntity();

        //map the piece-wise metadata
        scoreEntity.setFileDesignator(fileDesignator.toString());
        scoreEntity.setKeySignature(score.getKeySignature().getSignatureName());
        scoreEntity.setTimeSignature(score.getTimeSignature().toString());
        scoreEntity.setTempoIndication(score.getTempoIndication().getTempo());

        //map the tracks
        for(Track track : score.getTrackMap().values()){
            TrackEntity newTrack = new TrackEntity();
            newTrack.setInstrumentName(track.getInstrumentString());
            newTrack.setNotes(track.notesToString());
            scoreEntity.addTrack(newTrack);
        }

        //save the score
        ScoreEntity savedScore = scoreRepository.save(scoreEntity);

        return scoreEntity == savedScore;
    }

    /** Method to see if a designator already corresponds to an interpreted file
     *
     * @param fileDesignator the designator of the work
     * @return the existence and uniqueness of the interpreted work
     */
    public boolean scoreExists(UUID fileDesignator){
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(fileDesignator.toString());
        return searchResults.isPresent();
    }

}
