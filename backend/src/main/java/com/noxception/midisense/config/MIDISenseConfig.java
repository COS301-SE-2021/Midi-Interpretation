package com.noxception.midisense.config;

public class MIDISenseConfig {
    public static final String CROSS_ORIGIN = "*";
    public static final String FILE_FORMAT = ".mid";
    public static final String FILE_SYSTEM_PATH = "src/main/java/com/noxception/midisense/midiPool/";

    //EXCEPTIONS
    public static String messageBox(String message){ return "{"+message+"}";}
    public static final String EMPTY_REQUEST_EXCEPTION_TEXT = messageBox("No Request Made");
    public static final String FILE_TOO_LARGE_EXCEPTION_TEXT = messageBox("Maximum File Size Exceeded");
    public static final String EMPTY_FILE_EXCEPTION_TEXT = messageBox("Empty File");
    public static final String FILE_SYSTEM_EXCEPTION_TEXT = messageBox("File System Failure");
    public static final String INVALID_MIDI_EXCEPTION_TEXT = messageBox("MIDI Interpretation Failure");
}
