package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.intelligence.dataclass.ChordType;

public class AnalyseChordResponse extends ResponseObject {

    private byte rootPitch;
    private String chordType;
    private byte inversionPitch;
    private static final String[] toneMap = new String[]{"C","C#(Db)","D","D#(Eb)","E","F","F#(Gb)","G","G#(Ab)","A","A#(Bb)","B"};

    public AnalyseChordResponse() {
        this.rootPitch = 0;
        this.chordType = ChordType.MAJOR.getShortName();
        this.inversionPitch = 0;
    }

    public AnalyseChordResponse(byte rootPitch, String chordType) {
        this.rootPitch = rootPitch;
        this.chordType = chordType;
        this.inversionPitch = rootPitch;
    }

    public AnalyseChordResponse(byte rootPitch, String chordType, byte inversionPitch) {
        this.rootPitch = rootPitch;
        this.chordType = chordType;
        this.inversionPitch = inversionPitch;
    }

    public byte getRootPitch() {
        return rootPitch;
    }

    public void setRootPitch(byte rootPitch) {
        this.rootPitch = rootPitch;
    }

    public String getChordType() {
        return chordType;
    }

    public void setChordType(String chordType) {
        this.chordType = chordType;
    }

    public byte getInversionPitch() {
        return inversionPitch;
    }

    public void setInversionPitch(byte inversionPitch) {
        this.inversionPitch = inversionPitch;
    }

    public String getChord(){
        String root = toneMap[Math.floorMod(rootPitch,12)];
        String inversion = toneMap[Math.floorMod(inversionPitch,12)];
        String inversionString = (root.equals(inversion))?"":String.format(" / %s",inversion);
        return String.format("%s-%s",root,this.chordType)+inversionString;
    }
}
