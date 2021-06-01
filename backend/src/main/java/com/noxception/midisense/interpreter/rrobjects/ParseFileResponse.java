package com.noxception.midisense.interpreter.rrobjects;

public class ParseFileResponse {
    private String XMLString;
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
    }

}
