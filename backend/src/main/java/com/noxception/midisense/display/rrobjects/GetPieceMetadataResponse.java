package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;

public class GetPieceMetadataResponse extends ResponseObject {
    private final KeySignature keySignature;
    private  final TimeSignature timeSignature;
    private final TempoIndication tempoIndication;

    public GetPieceMetadataResponse(KeySignature keySignature, TimeSignature timeSignature, TempoIndication tempoIndication) {
        this.keySignature = keySignature;
        this.timeSignature = timeSignature;
        this.tempoIndication = tempoIndication;
    }

    public KeySignature getKeySignature() {
        return keySignature;
    }

    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    public TempoIndication getTempoIndication() {
        return tempoIndication;
    }
}
