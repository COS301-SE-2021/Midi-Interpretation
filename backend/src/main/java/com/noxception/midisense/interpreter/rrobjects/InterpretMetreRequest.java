package com.noxception.midisense.interpreter.rrobjects;

import java.util.UUID;

public class InterpretMetreRequest {
    private UUID fileDesignator;

    public InterpretMetreRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
