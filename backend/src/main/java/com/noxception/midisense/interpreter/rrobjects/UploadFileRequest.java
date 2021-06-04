package com.noxception.midisense.interpreter.rrobjects;

public class UploadFileRequest {
    private byte[] fileContents;

    public UploadFileRequest(byte[] fileContents) {
        this.fileContents = fileContents;
    }

    public byte[] getFileContents() {
        return fileContents;
    }
}
