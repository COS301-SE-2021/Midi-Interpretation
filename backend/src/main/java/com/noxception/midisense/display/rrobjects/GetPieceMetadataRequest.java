package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class GetPieceMetadataRequest extends RequestObject {
    private final UUID fileDesignator;

    public GetPieceMetadataRequest(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }
}
