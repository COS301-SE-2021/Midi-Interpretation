package com.noxception.midisense.interpreter.rrobjects;

public class ParseFileResponse {
    private boolean success;

    public ParseFileResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    //TODO: THIS IS WHAT I ACTUALLY WANT TO RETURN
    /*private String XMLString;
    private String staccatoSequence;

    public ParseFileResponse(String XMLString, String staccatoSequence) {
        this.XMLString = XMLString;
        this.staccatoSequence = staccatoSequence;
    }

    public String getXMLString() {
        return XMLString;
    }

    public String getStaccatoSequence() {
        return staccatoSequence;
    }*/

}
