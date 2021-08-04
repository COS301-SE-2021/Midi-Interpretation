package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class AnalyseTonalityRequest extends RequestObject {

    private final UUID fileDesignator;

    public AnalyseTonalityRequest(UUID fileDesignator) {this.fileDesignator = fileDesignator; }

    public UUID getFileDesignator() {
        return fileDesignator;
    }

}
