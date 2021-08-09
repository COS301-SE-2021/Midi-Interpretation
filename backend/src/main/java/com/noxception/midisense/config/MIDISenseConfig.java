package com.noxception.midisense.config;

import java.util.HashMap;
import java.util.Map;

public class MIDISenseConfig {

    public static Map<ConfigurationName, String> configurations = new HashMap<>();

    public static enum ConfigurationName {
            CROSS_ORIGIN,

            MIDI_STORAGE_ROOT,
            FILE_FORMAT,
            MAX_FILE_UPLOAD_SIZE,
            DELETE_UPON_INTERPRET,

            MIDI_TESTING_ROOT,
            MIDI_TESTING_FILE,
            MIDI_INVALID_TESTING_FILE,
            MIDI_TESTING_DESIGNATOR,
            MIDI_TESTING_TRACK_INDEX,

            MATRIX_WEIGHT_ROOT,

            EMPTY_REQUEST_EXCEPTION_TEXT,
            FILE_TOO_LARGE_EXCEPTION_TEXT,
            FILE_DOES_NOT_EXIST_EXCEPTION_TEXT,
            FILE_ALREADY_EXISTS_EXCEPTION_TEXT,
            EMPTY_FILE_EXCEPTION_TEXT,
            FILE_SYSTEM_EXCEPTION_TEXT,
            INVALID_MIDI_EXCEPTION_TEXT,
            INVALID_TRACK_INDEX_EXCEPTION_TEXT,

            SUCCESSFUL_PARSING_TEXT,

            MISSING_ANALYSIS_STRATEGY_EXCEPTION_TEXT
    };

    public static boolean isSet(ConfigurationName key){ return configurations.containsKey(key); }

    public static String configuration(ConfigurationName key){
        return configurations.get(key);
    }


}
