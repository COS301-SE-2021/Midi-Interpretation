package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

public class UploadFileRequest extends RequestObject {

    /** ATTRIBUTE */
    private final byte[] fileContents; //contents of midi file in the format of byte array

    /**
     * CONSTRUCTOR
     * @param fileContents byte array representation of midi file
     */
    public UploadFileRequest(byte[] fileContents) {
        this.fileContents = fileContents;
    }

    /**
     *  GET Method
     * @return fileContents
     */
    public byte[] getFileContents() {
        return fileContents;
    }
}
