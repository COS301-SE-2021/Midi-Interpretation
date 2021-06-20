package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetTrackInfoResponse extends ResponseObject {
    private final HashMap<Byte,String> trackMap;

    public GetTrackInfoResponse() {
        this.trackMap = new HashMap<>();
    }

    public GetTrackInfoResponse(HashMap<Byte, String> trackMap) {
        this.trackMap = trackMap;
    }

    public HashMap<Byte, String> getTrackMap() {
        return trackMap;
    }

    public void addTrack(byte trackIndex, String trackName){
        this.trackMap.put(trackIndex,trackName);
    }

    public ArrayList<Byte> getTrackIndices(){
        return new ArrayList<>(this.trackMap.keySet());
    }

    public String getTrack(byte trackIndex){
       return this.trackMap.get(trackIndex);
    }
}
