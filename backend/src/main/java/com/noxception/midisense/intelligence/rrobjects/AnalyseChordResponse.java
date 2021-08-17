package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class AnalyseChordResponse extends ResponseObject {
    /**
     * ATTRIBUTE
     */
    private final String chord; //genre classification

    /**
     * CONSTRUCTOR
     * @param c a chord within the piece
     */
    public AnalyseChordResponse(String c) { this.chord =c; }

    /**
     * GET method
     * @return chord
     */
    public String getChord() {
        return chord;
    }
}
