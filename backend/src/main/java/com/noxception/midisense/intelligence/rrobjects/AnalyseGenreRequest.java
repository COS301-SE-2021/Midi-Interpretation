package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class AnalyseGenreRequest extends RequestObject {

    private final UUID fileDesignator;

    public AnalyseGenreRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }

}
