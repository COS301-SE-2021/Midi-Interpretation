package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class InterpretTempoRequest extends RequestObject {

    /** ATTRIBUTE */
    private final UUID fileDesignator; //unique identifier for file

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify file
     */
    public InterpretTempoRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    /** GET Method
     * @return fileDesignator
     */
    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
