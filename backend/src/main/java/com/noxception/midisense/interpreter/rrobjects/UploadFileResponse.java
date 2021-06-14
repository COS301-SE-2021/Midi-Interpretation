package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.UUID;

public class UploadFileResponse extends ResponseObject {
    private final UUID fileDesignator;

    public UploadFileResponse(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }

}
