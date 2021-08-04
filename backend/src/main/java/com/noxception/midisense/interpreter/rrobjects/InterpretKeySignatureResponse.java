package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.KeySignature;

public class InterpretKeySignatureResponse extends ResponseObject {

    /** ATTRIBUTE */
    private final KeySignature keySignature; //Key signature of piece

    /**
     * CONSTRUCTOR
     * @param ks key signature of piece
     */
    public InterpretKeySignatureResponse(KeySignature ks) {
        this.keySignature = ks;
    }

    /**
     * CONSTRUCTOR
     * @param accidentals number of accidentals
     */
    public InterpretKeySignatureResponse(int accidentals){
        this.keySignature = new KeySignature(accidentals);
    }

    /**
     * CONSTRUCTOR
     * @param signatureName name of key signature
     */
    public InterpretKeySignatureResponse(String signatureName){
        this.keySignature = new KeySignature(signatureName);
    }

    /**
     *  GET Method
     * @return keySignature
     */
    public KeySignature getKeySignature() {
        return keySignature;
    }
}
