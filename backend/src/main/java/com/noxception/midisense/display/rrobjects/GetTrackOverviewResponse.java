package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;
import java.util.List;

public class GetTrackOverviewResponse extends ResponseObject {
    private final List<String> pitchArray;

    public GetTrackOverviewResponse(List<String> pitchArray) {
        this.pitchArray = pitchArray;
    }

    public List<String> getPitchArray() {
        return pitchArray;
    }
}
