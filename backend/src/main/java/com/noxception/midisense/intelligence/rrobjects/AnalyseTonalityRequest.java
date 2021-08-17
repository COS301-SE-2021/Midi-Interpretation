package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class AnalyseTonalityRequest extends RequestObject {

    /**
     * ATTRIBUTE
     */
    private final UUID fileDesignator;

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify the file
     */
    public AnalyseTonalityRequest(UUID fileDesignator) {this.fileDesignator = fileDesignator; }

    /**
     * GET method
     * @return fileDesignator
     */
    public UUID getFileDesignator() {
        return fileDesignator;
    }

}
