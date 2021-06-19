package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.DevelopmentNote;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.config.dataclass.LoggableObject;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.MIDISenseParserListener;
import com.noxception.midisense.interpreter.parser.Score;
import com.noxception.midisense.interpreter.parser.Track;
import com.noxception.midisense.interpreter.repository.NoteEntity;
import com.noxception.midisense.interpreter.repository.ScoreEntity;
import com.noxception.midisense.interpreter.repository.ScoreRepository;
import com.noxception.midisense.interpreter.repository.TrackEntity;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.midi.InvalidMidiDataException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class InterpreterServiceImpl extends LoggableObject implements InterpreterService{

    @Autowired
    private ScoreRepository scoreRepository;

    private final int maximumUploadSize = kilobytesToBytes(MIDISenseConfig.MAX_FILE_UPLOAD_SIZE);

    //================================
    // FRONT END USE CASES
    //================================

    @DevelopmentNote(
            taskName = "uploadFile Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.COMPLETED,
            lastModified = "2021/06/12",
            comments = "Successfully reconstructs and saves file."
    )
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
            File sourceFile = new File(generatePath(fileDesignator));

            //return response
            return new UploadFileResponse(fileDesignator);
        } catch (IOException e) {
            //throw exception
            throw new InvalidUploadException(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
        }
    }

    @DevelopmentNote(
            taskName = "processFile Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.IN_PROGRESS,
            lastModified = "2021/06/19",
            comments = "Added method"
    )
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
            log("Persisting Score "+fileDesignator.toString(),LogType.DEBUG);

            //delete file from storage
            //deleteFileFromStorage(fileDesignator);
            return new ProcessFileResponse(true,MIDISenseConfig.SUCCESSFUL_PARSING);
        }
        catch(InvalidMidiDataException m){
            //FILE CANT BE PARSED
            return new ProcessFileResponse(false, MIDISenseConfig.INVALID_MIDI_EXCEPTION_TEXT);
        }
        catch(InvalidDesignatorException d){
            //INVALID REFERENCE TO FILE
            return new ProcessFileResponse(false, MIDISenseConfig.FILE_DOES_NOT_EXIST);
        }
        /*catch(IOException i){
            //FILE SYSTEM FAILURE
            return new ProcessFileResponse(false, MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
        }*/

    }

    //================================
    // AUXILIARY METHODS
    //================================

    @DevelopmentNote(
            taskName = "interpretMetre Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.COMPLETED,
            lastModified = "2021/06/12 21:55",
            comments = "Reviewed method and tests~Claudio"
    )
    @Override
    public InterpretMetreResponse interpretMetre(InterpretMetreRequest request) throws InvalidDesignatorException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the metre
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST);
        String metre = searchResults.get().getTimeSignature();
        int numBeats = Integer.parseInt(metre.substring(0, metre.indexOf("/")));
        int beatValue = Integer.parseInt(metre.substring(metre.indexOf("/")+1));
        return new InterpretMetreResponse(numBeats,beatValue);
    }

    @DevelopmentNote(
            taskName = "interpretTempo Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.COMPLETED,
            lastModified = "2021/06/12 21:55",
            comments = "Reviewed method and tests~Claudio"
    )
    @Override
    public InterpretTempoResponse interpretTempo(InterpretTempoRequest request) throws InvalidDesignatorException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the tempo
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST);
        int tempoIndication = searchResults.get().getTempoIndication();
        return new InterpretTempoResponse(tempoIndication);
    }

    @DevelopmentNote(
            taskName = "interpretKeySignature Use Case",
            developers = {DevelopmentNote.Developers.CLAUDIO, DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/13 9:33",
            comments = "Finished method and testing"
    )
    @Override
    public InterpretKeySignatureResponse interpretKeySignature(InterpretKeySignatureRequest request) throws InvalidDesignatorException, InvalidKeySignatureException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the Key Signature
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST);
        String sigName = searchResults.get().getKeySignature();
        return new InterpretKeySignatureResponse(sigName);
    }

    @DevelopmentNote(
            taskName = "parseStaccato Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/12 21:55",
            comments = "Added method"
    )
    @Override
    public ParseStaccatoResponse parseStaccato(ParseStaccatoRequest request) throws InvalidDesignatorException{
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        try {
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(generatePath(fileDesignator));
            Pattern pattern  = MidiFileManager.loadPatternFromMidi(sourceFile);
            return new ParseStaccatoResponse(pattern.toString());
        } catch (IOException e) {
            throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST);
        } catch (InvalidMidiDataException e) {
            throw new InvalidDesignatorException(MIDISenseConfig.INVALID_MIDI_EXCEPTION_TEXT);
        }
    }

    @DevelopmentNote(
            taskName = "parseJSON Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.IN_PROGRESS,
            lastModified = "2021/06/17 22:24",
            comments = "Added method - relies upon a  custom parser listener. Will work on further"
    )
    @Override
    public ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidMidiDataException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        try {
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(generatePath(fileDesignator));
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

    private static int kilobytesToBytes(int n){
        return (int) (n*Math.pow(2,10));
    }

    private UUID writeFileToStorage(byte[] fileContents) throws IOException{
        //generate unique fileDesignator
        UUID fileDesignator = UUID.randomUUID();
        OutputStream os = new FileOutputStream(generatePath(fileDesignator));

        //write file to storage
        os.write(fileContents);
        os.close();

        //return Identifier
        return fileDesignator;
    }

    private void deleteFileFromStorage(UUID fileDesignator) throws IOException{
        File file = new File(generatePath(fileDesignator));
        if (!file.delete()) throw new IOException("Unable to delete file "+file.getName());
    }

    private String generatePath(UUID fileDesignator){
        //generates the path for the unique fileDesignator
        return MIDISenseConfig.FILE_SYSTEM_PATH+fileDesignator.toString()+ MIDISenseConfig.FILE_FORMAT;
    }

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
            //============================
           /* for(Note note: track.getNoteSequence()){
                NoteEntity newNote = new NoteEntity();
                newNote.setStep(note.getToneString());
                newNote.setOctave(note.getOctave());
                newNote.setOnVelocity(note.getOnVelocity());
                newNote.setOffVelocity(note.getOffVelocity());
                newNote.setRest(note.isRest());
                newNote.setPercussive(note.isPercussionNote());
                newTrack.addNote(note.toString());
            }*/
            newTrack.setNotes(track.notesToString());
            //============================
            scoreEntity.addTrack(newTrack);
        }

        //=======================================================
        ScoreEntity savedScore = scoreRepository.save(scoreEntity);
        return scoreEntity == savedScore;
    }

}
