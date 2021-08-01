package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class GetTrackMetadataRequest extends RequestObject {
    /**
     * ATTRIBUTES
     */
    private final UUID fileDesignator;
    private final byte trackIndex;

    /**
     * CONSTRUCTOR
     * @param fileDesignator used to identify the file
     * @param trackIndex used to identify the index of the song
     */
    public GetTrackMetadataRequest(UUID fileDesignator, byte trackIndex) {
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
     * @return the index of the track
     */
    public byte getTrackIndex() {
        return trackIndex;
    }
}
