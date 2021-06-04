package com.noxception.midisense.config;

public enum MidiSenseConfiguration {

    CROSSORIGIN("*"),

    FILEFORMAT(".mid"),
    FILESYSTEMPATH("src/main/java/com/noxception/midisense/midiPool/");

    private String value;
    MidiSenseConfiguration(String v){this.value=v;}
    public String get(){return this.value;}
}
