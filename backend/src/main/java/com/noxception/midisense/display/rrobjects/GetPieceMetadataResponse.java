package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.parser.KeySignature;
import com.noxception.midisense.interpreter.parser.TempoIndication;
import com.noxception.midisense.interpreter.parser.TimeSignature;

import java.util.List;

public class GetPieceMetadataResponse extends ResponseObject {

    /**
     * ATTRIBUTES
     */
    private final List<KeySignature> keySignatureMap;
    private  final List<TimeSignature> timeSignatureMap;
    private final List<TempoIndication> tempoIndicationMap;

    /**
     * CONSTRUCTOR
     * @param keySignatureMap key signature of the piece
     * @param timeSignatureMap time signature of the piece
     * @param tempoIndicationMap tempo indication of the piece
     */
    public GetPieceMetadataResponse(List<KeySignature> keySignatureMap, List<TimeSignature> timeSignatureMap, List<TempoIndication> tempoIndicationMap) {
        this.keySignatureMap = keySignatureMap;
        this.timeSignatureMap = timeSignatureMap;
        this.tempoIndicationMap = tempoIndicationMap;
    }

    /**
     * GET method
     * @return keySignature of the piece
     */
    public List<KeySignature> getKeySignature() {
        return keySignatureMap;
    }

    /**
     * GET method
     * @return timeSignature of the piece
     */
    public List<TimeSignature> getTimeSignature() {
        return timeSignatureMap;
    }

    /**
     * GET method
     * @return tempoIndication of the piece
     */
    public List<TempoIndication> getTempoIndication() {
        return tempoIndicationMap;
    }

}
