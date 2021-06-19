package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;

public class GetTrackOverviewResponse extends ResponseObject {
    private final ArrayList<Byte> pitchArray;

    public GetTrackOverviewResponse(ArrayList<Byte> pitchArray) {
        this.pitchArray = pitchArray;
    }

    public ArrayList<Byte> getPitchArray() {
        return pitchArray;
    }
}
