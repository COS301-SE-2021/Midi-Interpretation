package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.KeySignature;

public class InterpretKeySignatureResponse extends ResponseObject {
    private final KeySignature keySignature;

    public InterpretKeySignatureResponse(KeySignature ks) {
        this.keySignature = ks;
    }

    public InterpretKeySignatureResponse(int accidentals){
        this.keySignature = new KeySignature(accidentals);
    }

    public InterpretKeySignatureResponse(String signatureName){
        this.keySignature = new KeySignature(signatureName);
    }


    public KeySignature getKeySignature() {
        return keySignature;
    }
}
