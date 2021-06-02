package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MidiSenseConfiguration;
import com.noxception.midisense.interpreter.exceptions.InvalidParseException;
import com.noxception.midisense.interpreter.rrobjects.ParseFileRequest;
import com.noxception.midisense.interpreter.rrobjects.ParseFileResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class InterpreterServiceImpl implements InterpreterService{

    @Override
    public ParseFileResponse parseFile(ParseFileRequest request) throws InvalidParseException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidParseException("[Bad Request] No request made");
        String XMLString = null;
        String staccatoSequence = null;
        try {

            //get the contents of the file
            byte[] fileContents = request.getFileContents();

            //write to storage temporarily
            UUID fileDesignator = writeFileToStorage(fileContents);
            File sourceFile = new File(generatePath(fileDesignator));

            //translate
            /*Pattern pattern = MidiFileManager.loadPatternFromMidi(sourceFile);
            staccatoSequence = pattern.toString();*/

            /*MidiParser parser = new MidiParser();
            MusicXmlParserListener listener = new MusicXmlParserListener();
            parser.addParserListener(listener);
            parser.parse(MidiSystem.getSequence(sourceFile));
            XMLString = listener.getMusicXMLString();*/

            //remove from temporary storage
            deleteFileFromStorage(sourceFile);

        } catch (IOException e) {
            throw new InvalidParseException("[File System Failure] "+e.getMessage());
        }
        return new ParseFileResponse(true);
    }

    //================================
    // AUXILIARY METHODS
    //================================

    private UUID writeFileToStorage(byte[] fileContents) throws IOException{
        UUID fileDesignator = UUID.randomUUID();
        OutputStream os = new FileOutputStream(generatePath(fileDesignator));
        os.write(fileContents);
        os.close();
        return fileDesignator;
    }

    private void deleteFileFromStorage(File file) throws IOException{
        if (!file.delete()) throw new IOException("Unable to delete file \""+file.getName()+"\"");
    }

    private String generatePath(UUID fileDesignator){
        return MidiSenseConfiguration.FILESYSTEMPATH.get()+fileDesignator.toString()+ MidiSenseConfiguration.FILEFORMAT.get();
    }

}
