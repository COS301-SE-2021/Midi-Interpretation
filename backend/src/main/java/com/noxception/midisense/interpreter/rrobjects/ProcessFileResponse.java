package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class ProcessFileResponse extends ResponseObject {

    /** ATTRIBUTES */
    private final Boolean success; //success indicator
    private final String message; //response message

    /**
     * CONSTRUCTOR
     * @param success success indicator
     * @param message response message
     */
    public ProcessFileResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     *  GET Method
     * @return success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *  GET Method
     * @return message
     */
    public String getMessage() {
        return message;
    }
}
