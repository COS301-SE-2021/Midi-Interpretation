package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

public class UploadFileRequest extends RequestObject {
    private final byte[] fileContents;

    public UploadFileRequest(byte[] fileContents) {
        this.fileContents = fileContents;
    }

    public byte[] getFileContents() {
        return fileContents;
    }
}
