package com.noxception.midisense.interpreter.dataclass;

public class KeySignature {
    private String signatureName;
    private static final String[] majorSignatures =
            {"Cbmaj","Gbmaj","Dbmaj","Abmaj","Ebmaj","Bbmaj","Fmaj","Cmaj","Gmaj","Dmaj","Amaj","Emaj","Bmaj","F#maj","C#maj"};
    private static final String[] minorSignatures =
            {"Abmin","Ebmin","Bbmin","Fmin","Cmin","Gmin","Dmin","Amin","Emin","Bmin","F#min","C#min","G#min","D#min","A#min"};

    public KeySignature(){
        this.signatureName = "Cmaj";
    }

    public KeySignature(int numAccidentals){
        try{
            this.signatureName = majorSignatures[(numAccidentals)+7];
        }
        catch(IllegalArgumentException a){
            this.signatureName = "Cmaj";
        }
    }

    public KeySignature(int numAccidentals, boolean isMinor){
        try{
            if(isMinor)
                this.signatureName = minorSignatures[(numAccidentals)+7];
            else
                this.signatureName = majorSignatures[(numAccidentals)+7];
        }
        catch(IllegalArgumentException a){
            this.signatureName = "Cmaj";
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
