package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class GetTrackInfoRequest extends RequestObject {
    /**
     * ATTRIBUTE
     */
    private final UUID fileDesignator;

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify the file
     */
    public GetTrackInfoRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    /**
     * GET Method
     * @return fileDesignator
     */
    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
