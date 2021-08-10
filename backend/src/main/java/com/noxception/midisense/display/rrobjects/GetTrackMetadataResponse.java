package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class GetTrackMetadataResponse extends ResponseObject {

    /**
     * ATTRIBUTE
     */
    private final String trackString;

    /**
     * CONSTRUCTOR
     * @param trackString
     */
    public GetTrackMetadataResponse(String trackString) {
        this.trackString = trackString;
    }

    /**
     * GET method
     * @return trackString
     */
    public String getTrackString() {
        return trackString;
    }

}
