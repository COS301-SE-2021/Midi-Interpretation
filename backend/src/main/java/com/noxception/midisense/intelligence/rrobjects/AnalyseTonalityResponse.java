package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class AnalyseTonalityResponse extends ResponseObject {

    /**
     * ATTRIBUTE
     */
    private final String tonality; //genre classification

    /**
     * CONSTRUCTOR
     * @param t string representing tonality of the piece
     */
    public AnalyseTonalityResponse(String t) { this.tonality =t; }

    /**
     * GET method
     * @return tonality
     */
    public String getTonality() {
        return tonality;
    }
}
