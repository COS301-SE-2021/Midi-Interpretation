package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;

public class GetPieceMetadataResponse extends ResponseObject {

    /**
     * ATTRIBUTES
     */
    private final KeySignature keySignature;
    private  final TimeSignature timeSignature;
    private final TempoIndication tempoIndication;

    /**
     * CONSTRUCTOR
     * @param keySignature key signature of the piece
     * @param timeSignature time signature of the piece
     * @param tempoIndication tempo indication of the piece
     */
    public GetPieceMetadataResponse(KeySignature keySignature, TimeSignature timeSignature, TempoIndication tempoIndication) {
        this.keySignature = keySignature;
        this.timeSignature = timeSignature;
        this.tempoIndication = tempoIndication;
    }

    /**
     * GET method
     * @return keySignature of the piece
     */
    public KeySignature getKeySignature() {
        return keySignature;
    }

    /**
     * GET method
     * @return timeSignature of the piece
     */
    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    /**
     * GET method
     * @return tempoIndication of the piece
     */
    public TempoIndication getTempoIndication() {
        return tempoIndication;
    }

}
