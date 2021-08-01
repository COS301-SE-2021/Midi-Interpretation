package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;
import java.util.List;

public class GetTrackOverviewResponse extends ResponseObject {
    /**
     * ATTRIBUTE
     */
    private final List<String> pitchArray;

    /**
     * CONSTRUCTOR
     * @param pitchArray an array detailing the pitch of the piece
     */
    public GetTrackOverviewResponse(List<String> pitchArray) {
        this.pitchArray = pitchArray;
    }

    /**
     * GET method
     * @return pitchArray
     */
    public List<String> getPitchArray() {
        return pitchArray;
    }
}
