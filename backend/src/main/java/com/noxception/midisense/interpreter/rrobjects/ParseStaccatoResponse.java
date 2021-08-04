package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class ParseStaccatoResponse extends ResponseObject {

    /** ATTRIBUTE */
    private final String staccatoSequence; // Staccato sequence response object

    /**
     * CONSTRUCTOR
     * @param staccatoSequence sequence in Staccato format
     */
    public ParseStaccatoResponse(String staccatoSequence) {
        this.staccatoSequence = staccatoSequence;
    }

    /**
     *  GET Method
     * @return staccatoSequence
     */
    public String getStaccatoSequence() {
        return staccatoSequence;
    }
}
