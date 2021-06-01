package com.noxception.midisense.interpreter.rrobjects;

public class ParseFileRequest {
    private byte[] fileContents;

    public ParseFileRequest(byte[] fileContents) {
        this.fileContents = fileContents;
    }

    public byte[] getFileContents() {
        return fileContents;
    }
}
