package com.noxception.midisense.interpreter.dataclass;

public class KeySignature {
    private String signatureName;
    private static final String[] signatures =
            {"Cbmaj","Gbmaj","Dbmaj","Abmaj","Ebmaj","Bbmaj","Cmaj","Gmaj","Dmaj","Amaj","Emaj","Bmaj","F#maj","C#maj"};

    public KeySignature(){
        this.signatureName = "Cmaj";
    }

    public KeySignature(int numAccidentals){
        try{
            this.signatureName = signatures[(numAccidentals)+7];
        }
        catch(IllegalArgumentException a){
            this.signatureName = "Unknown";
        }
    }
    public KeySignature(String signatureName){
        this.signatureName = signatureName;
    }

    public String getSignatureName() {
        return signatureName;
    }

    @Override
    public String toString() {
        return getSignatureName();
    }

}
