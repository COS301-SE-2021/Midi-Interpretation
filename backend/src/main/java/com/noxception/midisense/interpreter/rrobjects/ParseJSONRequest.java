package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class ParseJSONRequest extends RequestObject {
    private final UUID fileDesignator;

    public ParseJSONRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }
}