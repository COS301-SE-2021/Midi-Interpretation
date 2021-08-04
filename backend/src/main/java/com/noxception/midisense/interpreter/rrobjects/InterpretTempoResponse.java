package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;

public class InterpretTempoResponse extends ResponseObject {

    /** ATTRIBUTE */
    private final TempoIndication tempo; //tempo response object

    /**
     * CONSTRUCTOR
     * @param tempo tempo object of the piece response
     */
    public InterpretTempoResponse(TempoIndication tempo) {
        this.tempo = tempo;
    }

    /**
     * CONSTRUCTOR
     * @param tempo tempo integer of the piece
     */
    public InterpretTempoResponse(int tempo) {
        this.tempo = new TempoIndication(tempo);
    }

    /**
     *  GET Method
     * @return tempo
     */
    public TempoIndication getTempo() {
        return tempo;
    }
}
