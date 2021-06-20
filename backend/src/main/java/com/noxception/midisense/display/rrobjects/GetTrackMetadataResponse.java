package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class GetTrackMetadataResponse extends ResponseObject {
    //TODO: update to be a more comprehensive class once settled on structure for track
    private final String trackString;

    public GetTrackMetadataResponse(String trackString) {
        this.trackString = trackString;
    }

    public String getTrackString() {
        return trackString;
    }
}
