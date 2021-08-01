package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class GetPieceMetadataRequest extends RequestObject {
    /**
     * ATTRIBUTE
     */

    private final UUID fileDesignator;

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify file
     */
    public GetPieceMetadataRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    /**
     * GET METHOD
     * @return fileDesignator
     */
    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
