package com.noxception.midisense.intelligence.dataclass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChordPrediction {
    private byte rootNote;
    private byte bassNote;
    private ChordType chordType;
    private final byte numAccidentals = 0;
    private static final String[] toneMap = new String[]{"C","C#(Db)","D","D#(Eb)","E","F","F#(Gb)","G","G#(Ab)","A","A#(Bb)","B"};
    private static final Map<Integer, Set<String>> accidentalLookup= new HashMap<>();

    public ChordPrediction(byte rootNote, ChordType chordType) {
        this.rootNote = rootNote;
        this.chordType = chordType;
        this.bassNote = rootNote;
    }

    public ChordPrediction(byte rootNote, byte bassNote, ChordType chordType) {
        this.rootNote = rootNote;
        this.bassNote = bassNote;
        this.chordType = chordType;
    }

    public byte getRootNote() {
        return rootNote;
    }

    public void setRootNote(byte rootNote) {
        this.rootNote = rootNote;
    }

    public ChordType getChordType() {
        return chordType;
    }

    public void setChordType(ChordType chordType) {
        this.chordType = chordType;
    }

    public String getCommonName(){
        String root = toneMap[rootNote % 12];
        String inversion = toneMap[bassNote % 12];
        String type = this.chordType.getShortName();

        String inversionString = (root.equals(inversion))?"":String.format(" / %s",inversion);

        return String.format("%s-%s",root,type)+inversionString;
    }
}