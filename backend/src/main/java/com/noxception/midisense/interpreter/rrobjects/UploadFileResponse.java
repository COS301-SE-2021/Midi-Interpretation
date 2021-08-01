package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.UUID;

public class UploadFileResponse extends ResponseObject {

    /** ATTRIBUTE */
    private final UUID fileDesignator; //unique identifier for file

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify file
     */
    public UploadFileResponse(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    /**
     *  GET Method
     * @return fileDesignator
     */
    public UUID getFileDesignator() {
        return fileDesignator;
    }

}
