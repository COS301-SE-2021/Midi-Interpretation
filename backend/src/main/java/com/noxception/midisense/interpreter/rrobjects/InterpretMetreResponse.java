package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;

public class InterpretMetreResponse extends ResponseObject {

    /** ATTRIBUTE */
    private final TimeSignature metre; //response rythmic pattern of the piece

    /**
     * CONSTRUCTOR
     * @param metre rythmic pattern of the piece
     */
    public InterpretMetreResponse(TimeSignature metre) {
        this.metre = metre;
    }

    /**
     * CONSTRUCTOR
     * @param numBeats numerator of metre
     * @param beatValue denominator of metre
     */
    public InterpretMetreResponse(int numBeats, int beatValue) {
        this.metre = new TimeSignature(numBeats,beatValue);
    }

    /**
     *  GET Method
     * @return metre
     */
    public TimeSignature getMetre() {
        return metre;
    }
}
