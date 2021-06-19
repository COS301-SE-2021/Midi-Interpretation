package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class GetTrackOverviewRequest extends RequestObject {
    private final UUID fileDesignator;
    private final byte trackIndex;

    public GetTrackOverviewRequest(UUID fileDesignator, byte trackIndex) {
        this.fileDesignator = fileDesignator;
        this.trackIndex = trackIndex;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }

    public byte getTrackIndex() {
        return trackIndex;
    }
}
