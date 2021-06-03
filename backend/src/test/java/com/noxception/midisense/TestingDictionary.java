package com.noxception.midisense;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestingDictionary {
    //naming convention is <subsystem>_<use case>_<association>

    //Interpreter
    public static byte[] interpreter_uploadFile_validFileContents = getValidByteArray();
    public static byte[] interpreter_uploadFile_invalidFileContents = {1,2,3};



    //MISC methods
    public static byte[] getValidByteArray(){
        try {
            return Files.readAllBytes(Paths.get("src/main/java/com/noxception/midisense/midiPool/MyHeartWillGoOn.mid"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
