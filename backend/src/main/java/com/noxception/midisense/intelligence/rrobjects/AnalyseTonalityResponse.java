package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class AnalyseTonalityResponse extends ResponseObject {

    private final String tonality; //genre classification

    public AnalyseTonalityResponse(String t) { this.tonality =t; }

    public String getTonality() {
        return tonality;
    }
}
