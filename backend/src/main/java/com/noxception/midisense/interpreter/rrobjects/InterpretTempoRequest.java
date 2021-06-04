package com.noxception.midisense.interpreter.rrobjects;

import java.util.UUID;

public class InterpretTempoRequest {
    private UUID fileDesignator;

    public InterpretTempoRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
