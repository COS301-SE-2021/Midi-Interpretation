package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MidiSenseConfiguration;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class InterpreterServiceImpl implements InterpreterService{

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) throws InvalidUploadException {
        //check to see if there is a valid request object
        if(request==null) throw new InvalidUploadException("[Bad Request] No request made");
        try {

            //get the contents of the file
            byte[] fileContents = request.getFileContents();

            //write to storage
            UUID fileDesignator = writeFileToStorage(fileContents);
            File sourceFile = new File(generatePath(fileDesignator));

            //return response
            return new UploadFileResponse(fileDesignator);
        } catch (IOException e) {
            //throw exception
            throw new InvalidUploadException("[File System Failure] ");
        }
    }

            //TODO: Future code for interpretation
            //translate
            /*Pattern pattern = MidiFileManager.loadPatternFromMidi(sourceFile);
            staccatoSequence = pattern.toString();*/

            /*MidiParser parser = new MidiParser();
            MusicXmlParserListener listener = new MusicXmlParserListener();
            parser.addParserListener(listener);
            parser.parse(MidiSystem.getSequence(sourceFile));
            XMLString = listener.getMusicXMLString();*/

    //================================
    // AUXILIARY METHODS
    //================================

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
