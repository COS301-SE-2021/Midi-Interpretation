package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.interpreter.dataclass.TimeSignature;

public class InterpretMetreResponse {
    private TimeSignature metre;

    public InterpretMetreResponse(TimeSignature metre) {
        this.metre = metre;
    }

    public InterpretMetreResponse(int numBeats, int beatValue) {
        this.metre = new TimeSignature(numBeats,beatValue);
    }

    public TimeSignature getMetre() {
        return metre;
    }
}
