package com.noxception.midisense.interpreter.rrobjects;

import java.util.UUID;

public class UploadFileResponse {
    private UUID fileDesignator;

    public UploadFileResponse(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
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
