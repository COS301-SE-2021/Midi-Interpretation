package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class InterpretMetreRequest extends RequestObject {
    private final UUID fileDesignator;

    public InterpretMetreRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
