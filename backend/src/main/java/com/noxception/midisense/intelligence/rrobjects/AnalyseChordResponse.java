package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class AnalyseChordResponse extends ResponseObject {
    //chord name

    private final String chord; //genre classification

    public AnalyseChordResponse(String c) { this.chord =c; }

    public String getChord() {
        return chord;
    }
}
