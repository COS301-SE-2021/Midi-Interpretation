package com.noxception.midisense.display.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetTrackInfoResponse extends ResponseObject {
    /**
     * ATTRIBUTE
     */
    private final HashMap<Byte,String> trackMap;

    /**
     * CONSTRUCTOR
     */
    public GetTrackInfoResponse() {
        this.trackMap = new HashMap<>();
    }

    /**
     * CONSTRUCTOR
     * @param trackMap the mapped byte array of the track
     */
    public GetTrackInfoResponse(HashMap<Byte, String> trackMap) {
        this.trackMap = trackMap;
    }

    /**
     * GET method
     * @return the track map of the song
     */
    public HashMap<Byte, String> getTrackMap() {
        return trackMap;
    }

    /**
     * Adding a track to the trackMap
     * @param trackIndex the index the track will be inserted to
     * @param trackName the name of the track
     */
    public void addTrack(byte trackIndex, String trackName){
        this.trackMap.put(trackIndex,trackName);
    }

    /**
     * GET method
     * @return a list of all the track indices
     */
    public ArrayList<Byte> getTrackIndices(){
        return new ArrayList<>(this.trackMap.keySet());
    }

    /**
     * GET method
     * @param trackIndex the tracks' index
     * @return the track associated with said track
     */
    public String getTrack(byte trackIndex){
       return this.trackMap.get(trackIndex);
    }
}
