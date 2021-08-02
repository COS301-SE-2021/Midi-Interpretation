package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class GetTrackOverviewRequest extends RequestObject {

    /**
     * ATTRIBUTES
     */
    private final UUID fileDesignator;
    private final byte trackIndex;

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify the file
     * @param trackIndex used to identify the position of the track
     */
    public GetTrackOverviewRequest(UUID fileDesignator, byte trackIndex) {
        this.fileDesignator = fileDesignator;
        this.trackIndex = trackIndex;
    }

    /**
     * GET method
     * @return fileDesignator
     */
    public UUID getFileDesignator() {
        return fileDesignator;
    }

    /**
     * GET method
     * @return the tracks index
     */
    public byte getTrackIndex() {
        return trackIndex;
    }

}
