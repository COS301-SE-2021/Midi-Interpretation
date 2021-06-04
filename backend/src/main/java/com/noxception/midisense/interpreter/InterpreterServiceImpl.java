package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MidiSenseConfiguration;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
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

    private final int maximumUploadSize = kilobytesToBytes(36);

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) throws InvalidUploadException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidUploadException("[No Request Made]");
        try {

            //get the contents of the file
            byte[] fileContents = request.getFileContents();
            //check to see that it isn't empty, or is of maximum length
            if(fileContents.length==0) throw new InvalidUploadException("[Empty File]");
            if(fileContents.length > maximumUploadSize) throw new InvalidUploadException("[Maximum File Size Exceeded]");

            //write to storage
            UUID fileDesignator = writeFileToStorage(fileContents);
            File sourceFile = new File(generatePath(fileDesignator));

            //return response
            return new UploadFileResponse(fileDesignator);
        } catch (IOException e) {
            //throw exception
            throw new InvalidUploadException("[File System Failure]");
        }
    }

    @Override
    public InterpretMetreResponse interpretMetre(InterpretMetreRequest request) throws InvalidDesignatorException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException("[No Request Made]");
        try {
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(generatePath(fileDesignator));
            Pattern pattern  = MidiFileManager.loadPatternFromMidi(sourceFile);
            String details = pattern.toString();
            String time = details.substring(details.indexOf("TIME:")+5,details.indexOf("KEY:")-1);
            int numBeats = Integer.parseInt(time.substring(0,time.indexOf("/")));
            int beatValue = Integer.parseInt(time.substring(time.indexOf("/")+1));
            return new InterpretMetreResponse(numBeats,beatValue);
        } catch (IOException e) {
            throw new InvalidDesignatorException("[File System Failure]");
        } catch (InvalidMidiDataException e) {
            throw new InvalidDesignatorException("[MIDI Interpretation Failure]");
        }
    }

    @Override
    public InterpretTempoResponse interpretTempo(InterpretTempoRequest request) throws InvalidDesignatorException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidDesignatorException("[No Request Made]");
        try {
            UUID fileDesignator = request.getFileDesignator();
            File sourceFile = new File(generatePath(fileDesignator));
            Pattern pattern  = MidiFileManager.loadPatternFromMidi(sourceFile);
            String details = pattern.toString();
            String tempo = details.substring(1,details.indexOf("TIME:")-1);
            int tempoIndication = Integer.parseInt(tempo);
            return new InterpretTempoResponse(tempoIndication);
        } catch (IOException e) {
            throw new InvalidDesignatorException("[File System Failure]");
        } catch (InvalidMidiDataException e) {
            throw new InvalidDesignatorException("[MIDI Interpretation Failure]");
        }
    }

            //TODO: Future code for interpretation
            //translate
            /*Pattern pattern = MidiFileManager.loadPatternFromMidi(sourceFile);
            staccatoSequence = pattern.toString();


            MidiParser parser = new MidiParser();
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
        if (!file.delete()) throw new IOException("Unable to delete file \""+file.getName()+"\"");
    }

    private String generatePath(UUID fileDesignator){
        //generates the path for the unique fileDesignator
        return MidiSenseConfiguration.FILESYSTEMPATH.get()+fileDesignator.toString()+ MidiSenseConfiguration.FILEFORMAT.get();
    }

}
