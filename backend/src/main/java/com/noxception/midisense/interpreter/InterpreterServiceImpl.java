package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.config.DevelopmentNote;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.springframework.stereotype.Service;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class InterpreterServiceImpl implements InterpreterService{

    private final int maximumUploadSize = kilobytesToBytes(1024);

    @DevelopmentNote(
            taskName = "uploadFile Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.COMPLETED,
            lastModified = "2021/06/12",
            comments = "Successfully reconstructs and saves file."
    )
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
            taskName = "interpretMetre Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/12 21:55",
            comments = "Updated to use the new parse staccato method."
    )
    @Override
    public InterpretMetreResponse interpretMetre(InterpretMetreRequest request) throws InvalidDesignatorException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the metre
        String details = parseStaccato(new ParseStaccatoRequest(request.getFileDesignator())).getStaccatoSequence();
        String time = details.substring(details.indexOf("TIME:")+5,details.indexOf("KEY:")-1);
        int numBeats = Integer.parseInt(time.substring(0,time.indexOf("/")));
        int beatValue = Integer.parseInt(time.substring(time.indexOf("/")+1));
        return new InterpretMetreResponse(numBeats,beatValue);
    }

    @DevelopmentNote(
            taskName = "interpretTempo Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/12 21:55",
            comments = "Updated to use the new parse staccato method."
    )
    @Override
    public InterpretTempoResponse interpretTempo(InterpretTempoRequest request) throws InvalidDesignatorException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the tempo
        String details = parseStaccato(new ParseStaccatoRequest(request.getFileDesignator())).getStaccatoSequence();
        String tempo = details.substring(1,details.indexOf("TIME:")-1);
        int tempoIndication = Integer.parseInt(tempo);
        return new InterpretTempoResponse(tempoIndication);
    }

    @DevelopmentNote(
            taskName = "interpretKeySignature Use Case",
            developers = {DevelopmentNote.Developers.CLAUDIO, DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/12 21:55",
            comments = "Updated to use the new parse staccato method. Needs Tests"
    )
    @Override
    public InterpretKeySignatureResponse interpretKeySignature(InterpretKeySignatureRequest request) throws InvalidDesignatorException, InvalidKeySignatureException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        //attempt to interpret the Key Signature
        String details = parseStaccato(new ParseStaccatoRequest(request.getFileDesignator())).getStaccatoSequence();
        String sigName = details.substring(details.indexOf("KEY:")+4, details.substring(details.indexOf("KEY:")).indexOf(" ")+details.indexOf("KEY:"));
        return new InterpretKeySignatureResponse(sigName);
    }

    @DevelopmentNote(
            taskName = "parseStaccato Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/12 21:55",
            comments = "Added method - needs tests"
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
            throw new InvalidDesignatorException(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
        } catch (InvalidMidiDataException e) {
            throw new InvalidDesignatorException(MIDISenseConfig.INVALID_MIDI_EXCEPTION_TEXT);
        }
    }

            //TODO: Future code for interpretation


           /* MidiParser parser = new MidiParser();
            MusicXmlParserListener listener = new MusicXmlParserListener();
            parser.addParserListener(listener);
            parser.parse(MidiSystem.getSequence(sourceFile));
            XMLString = listener.getMusicXMLString();*/


    //================================
    // AUXILIARY METHODS
    //================================

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

    private void deleteFileFromStorage(File file) throws IOException{
        if (!file.delete()) throw new IOException("Unable to delete file "+file.getName());
    }

    private String generatePath(UUID fileDesignator){
        //generates the path for the unique fileDesignator
        return MIDISenseConfig.FILE_SYSTEM_PATH+fileDesignator.toString()+ MIDISenseConfig.FILE_FORMAT;
    }

}
