package com.noxception.midisense.interpreter.dataclass;

import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;

public class KeySignature {
    private final int numAccidentals;
    private final String signatureName;
    private static final String[] signatures =
            {"Cbmaj","Gbmaj","Dbmaj","Abmaj","Ebmaj","Bbmaj","Cmaj","Gmaj","Dmaj","Amaj","Emaj","Bmaj","F#maj","C#maj"};

    public KeySignature(){
        this.numAccidentals = 0;
        this.signatureName = "Cmaj";
    }

    public KeySignature(int numAccidentals) throws InvalidKeySignatureException {
        this.numAccidentals = numAccidentals;
        this.signatureName = numAccidentalsToSignatureName(numAccidentals);
    }
    public KeySignature(String signatureName) throws InvalidKeySignatureException{
        this.numAccidentals = signatureNameToNumAccidentals(signatureName);
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

    private String numAccidentalsToSignatureName(int numAccidentals) throws InvalidKeySignatureException{
       try{ return signatures[(7+numAccidentals)-1];}
       catch(Exception e){
            throw new InvalidKeySignatureException("[Not a valid Key Signature]");
       }
    }

    private int signatureNameToNumAccidentals(String signatureName) throws InvalidKeySignatureException{
        for(int i=0; i<signatures.length; i++){
            if(signatures[i].contains(signatureName)) return i;
        }
        throw new InvalidKeySignatureException("[Not a valid Key Signature]");
    }

}
