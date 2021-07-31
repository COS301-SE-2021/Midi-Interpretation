package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.config.dataclass.LoggableObject;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.midi.InvalidMidiDataException;
import javax.transaction.Transactional;
import java.io.*;
import java.util.Optional;
import java.util.UUID;

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
public class InterpreterServiceImpl extends LoggableObject implements InterpreterService{


    @Autowired
    private ScoreRepository scoreRepository;

    private final int maximumUploadSize = kilobytesToBytes(MIDISenseConfig.MAX_FILE_UPLOAD_SIZE);

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
        //check to see if there is a valid request object
        if(request==null) throw new InvalidUploadException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        try {
           //get the contents of the file
            byte[] fileContents = request.getFileContents();
            //check to see that it isn't empty, or is of maximum length
            if(fileContents.length==0) throw new InvalidUploadException(MIDISenseConfig.EMPTY_FILE_EXCEPTION_TEXT);
            if(fileContents.length > maximumUploadSize) throw new InvalidUploadException(MIDISenseConfig.FILE_TOO_LARGE_EXCEPTION_TEXT);

            //write to storage
            UUID fileDesignator = writeFileToStorage(fileContents);
            return new UploadFileResponse(fileDesignator);

        } catch (IOException e) {
            //throw exception
            throw new InvalidUploadException(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
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
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        try {
            //parse file as JSON and persist to database
            UUID fileDesignator = request.getFileDesignator();
            ParseJSONResponse parserResponse = this.parseJSON(new ParseJSONRequest(fileDesignator));
            Score parsedScore = parserResponse.getParsedScore();

            //persist score details to database
            saveScore(parsedScore,fileDesignator);

            if(MIDISenseConfig.DELETE_UPON_INTERPRET) deleteFileFromStorage(fileDesignator);
            return new ProcessFileResponse(true,MIDISenseConfig.SUCCESSFUL_PARSING_TEXT);
        }
        catch(InvalidMidiDataException m){
            //FILE CANT BE PARSED
            return new ProcessFileResponse(false, MIDISenseConfig.INVALID_MIDI_EXCEPTION_TEXT);
        }
        catch(InvalidDesignatorException d){
            //INVALID REFERENCE TO FILE
            return new ProcessFileResponse(false, MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
        }
        catch(IOException i){
            //FILE SYSTEM FAILURE
            return new ProcessFileResponse(false, MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
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
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the metre
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
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
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the tempo
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
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
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the Key Signature
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
        String sigName = searchResults.get().getKeySignature();
        return new InterpretKeySignatureResponse(sigName);
    }

    /**Produces a human-readable string representation of a midi file stored in temporary storage
     *
     * @param request an object encapsulating a reference to the file uploaded in temporary storage
     * @return an object encapsulating the string representation of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public ParseStaccatoResponse parseStaccato(ParseStaccatoRequest request) throws InvalidDesignatorException{
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        try {
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(MIDISenseConfig.MIDI_FILE_STORAGE+fileDesignator+".mid");
            Pattern pattern  = MidiFileManager.loadPatternFromMidi(sourceFile);
            return new ParseStaccatoResponse(pattern.toString());
        } catch (IOException e) {
            throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
        } catch (InvalidMidiDataException e) {
            throw new InvalidDesignatorException(MIDISenseConfig.INVALID_MIDI_EXCEPTION_TEXT);
        }
    }

    /**Produces a complete tree-based representation of a midi file and its track sequences stored in temporary storage
     *
     * @param request an object encapsulating a reference to the file uploaded in temporary storage
     * @return an object encapsulating the tempo indication of the interpreted work
     * @throws InvalidDesignatorException if the designator does not refer to an existing file in persisted storage
     */
    @Override
    public ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidMidiDataException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        try {
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(MIDISenseConfig.MIDI_FILE_STORAGE+fileDesignator+".mid");
            MidiParser parser = new MidiParser();
            MIDISenseParserListener listener = new MIDISenseParserListener();
            parser.addParserListener(listener);
            parser.parse(MidiFileManager.load(sourceFile));
            return new ParseJSONResponse(listener.getParsedScore());
        } catch (IOException e) {
            throw new InvalidDesignatorException(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
        } catch (InvalidMidiDataException e) {
            throw new InvalidMidiDataException(MIDISenseConfig.INVALID_MIDI_EXCEPTION_TEXT);
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
        String fileName = MIDISenseConfig.MIDI_FILE_STORAGE+fileDesignator+".mid";
        FileOutputStream os = new FileOutputStream(fileName);
        os.write(fileContents);
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
        File file = new File(MIDISenseConfig.MIDI_FILE_STORAGE+fileDesignator+".mid");
        if (!file.delete()) throw new IOException("Unable to delete file "+file.getName());
    }


    /** Persists a score object to an external repository
     *
     * @param score encapsulates the interpreted midi file
     * @param fileDesignator the unique identifier corresponding to the file in temporary storage
     * @return whether or not the score is successfully persisted
     */
    @Transactional
    public boolean saveScore(Score score, UUID fileDesignator){
        ScoreEntity scoreEntity = new ScoreEntity();
        //map the variables
        scoreEntity.setFileDesignator(fileDesignator.toString());
        scoreEntity.setKeySignature(score.getKeySignature().getSignatureName());
        scoreEntity.setTimeSignature(score.getTimeSignature().toString());
        scoreEntity.setTempoIndication(score.getTempoIndication().getTempo());
        for(Track track : score.getTrackMap().values()){
            TrackEntity newTrack = new TrackEntity();
            newTrack.setInstrumentName(track.getInstrumentString());
            newTrack.setNotes(track.notesToString());
            scoreEntity.addTrack(newTrack);
        }
        ScoreEntity savedScore = scoreRepository.save(scoreEntity);
        return scoreEntity == savedScore;
    }

}
