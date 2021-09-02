package com.noxception.midisense.config;

/**
 * Enumeration that governs which properties are loaded from the environment upon initialisation.
 */
public enum ConfigurationName {

    //handles request logic
    CROSS_ORIGIN,

    //handles file storage logic
    MIDI_STORAGE_ROOT,
    FILE_FORMAT,
    MAX_FILE_UPLOAD_SIZE,
    DELETE_UPON_INTERPRET,

    //handles interpretation
    MIDI_INTERPRETATION_SCRIPT_PATH,
    MIDI_INTERPRETATION_URL,
    MIDI_INTERPRETATION_TIMEOUT,

    //handles testing
    MIDI_TESTING_ROOT,
    MIDI_TESTING_FILE,
    MIDI_INVALID_TESTING_FILE,
    MIDI_TESTING_DESIGNATOR,
    MIDI_TESTING_TRACK_INDEX,

    //handles AI parametrisation
    MATRIX_WEIGHT_ROOT,

    //Exception messages
    EMPTY_REQUEST_EXCEPTION_TEXT,
    FILE_TOO_LARGE_EXCEPTION_TEXT,
    FILE_DOES_NOT_EXIST_EXCEPTION_TEXT,
    FILE_ALREADY_EXISTS_EXCEPTION_TEXT,
    EMPTY_FILE_EXCEPTION_TEXT,
    FILE_SYSTEM_EXCEPTION_TEXT,
    INVALID_MIDI_EXCEPTION_TEXT,
    INVALID_MIDI_TIMEOUT_EXCEPTION_TEXT,
    INVALID_TRACK_INDEX_EXCEPTION_TEXT,
    MISSING_ANALYSIS_STRATEGY_EXCEPTION_TEXT,


    //Success messages
    SUCCESSFUL_PARSING_TEXT



}
