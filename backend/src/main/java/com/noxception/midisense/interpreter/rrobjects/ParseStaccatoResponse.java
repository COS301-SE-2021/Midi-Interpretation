package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class ParseStaccatoResponse extends ResponseObject {
    private final String staccatoSequence;

    public ParseStaccatoResponse(String staccatoSequence) {
        this.staccatoSequence = staccatoSequence;
    }

    public String getStaccatoSequence() {
        return staccatoSequence;
    }
}
