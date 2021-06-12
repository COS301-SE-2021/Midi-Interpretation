package com.noxception.midisense.interpreter.dataclass;

public class KeySignature {
    private int numAccidentals;
    private String signatureName;

    public KeySignature(int numAccidentals) {
        this.numAccidentals = numAccidentals;
        this.signatureName = "lololo";//TODO: Adrian fill this in
    }
    public KeySignature(String signatureName) {
        this.numAccidentals = numAccidentals;//TODO: Adrian fill this in
        this.signatureName = signatureName;
    }

    public int getNumAccidentals() {
        return numAccidentals;
    }

    public String getSignatureName() {
        return signatureName;
    }

    @Override
    public String toString() {
        return getSignatureName();
    }
}
