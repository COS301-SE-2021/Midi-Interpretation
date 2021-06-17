package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class ParseXMLResponse extends ResponseObject {
    private final String XMLSequence;

    public ParseXMLResponse(String XMLSequence) {
        this.XMLSequence = XMLSequence;
    }

    public String getXMLSequence() {
        return XMLSequence;
    }
}
