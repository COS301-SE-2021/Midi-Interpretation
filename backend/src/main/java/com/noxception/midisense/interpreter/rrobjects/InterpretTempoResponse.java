package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;

public class InterpretTempoResponse extends ResponseObject {
    private final TempoIndication tempo;

    public InterpretTempoResponse(TempoIndication tempo) {
        this.tempo = tempo;
    }

    public InterpretTempoResponse(int tempo) {
        this.tempo = new TempoIndication(tempo);
    }

    public TempoIndication getTempo() {
        return tempo;
    }
}
