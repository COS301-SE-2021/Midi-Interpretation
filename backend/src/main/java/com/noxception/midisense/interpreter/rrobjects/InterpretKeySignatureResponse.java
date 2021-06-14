package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;

public class InterpretKeySignatureResponse extends ResponseObject {
    private final KeySignature keySignature;

    public InterpretKeySignatureResponse(KeySignature ks) {
        this.keySignature = ks;
    }

    public InterpretKeySignatureResponse(int accidentals) throws InvalidKeySignatureException {
        this.keySignature = new KeySignature(accidentals);
    }

    public InterpretKeySignatureResponse(String signatureName) throws InvalidKeySignatureException{
        this.keySignature = new KeySignature(signatureName);
    }


    public KeySignature getKeySignature() {
        return keySignature;
    }
}
