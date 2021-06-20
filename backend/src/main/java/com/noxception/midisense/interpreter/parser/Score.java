package com.noxception.midisense.interpreter.parser;

import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Score {
    private final HashMap<Integer,Track> trackMap = new HashMap<>();
    private KeySignature keySignature;
    private TimeSignature timeSignature;
    private TempoIndication tempoIndication;

    public void addTrack(int trackNumber){
        trackMap.put(trackNumber,new Track());
    }

    public void addTrack(int trackNumber, Track track){
        trackMap.put(trackNumber,track);
    }

    public void setKeySignature(byte x, byte y){
        this.keySignature = new KeySignature();
    }

    public void setTimeSignature(byte x, byte y){
        this.timeSignature = new TimeSignature((int) x, (int) Math.pow(2,(int) y));
    }

    public void setTempoIndication(int tempoIndication){
        this.tempoIndication = new TempoIndication(tempoIndication);
    }

    public Track getTrack(int trackNumber){
        return trackMap.get(trackNumber);
    }

    public HashMap<Integer, Track> getTrackMap() {
        return trackMap;
    }

    public KeySignature getKeySignature() {
        return keySignature;
    }

    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    public TempoIndication getTempoIndication() {
        return tempoIndication;
    }

    @Override
    public String toString() {
        List<String> trackList = new ArrayList<>();
        for(Track t : trackMap.values()){
            trackList.add(t.toString());
        }
        return String.format("{\"key_signature\": \"%s\", \"time_signature\": \"%s\", \"tempo_indication\": %d, \"tracks\": [%s]}",this.keySignature,this.timeSignature,this.tempoIndication.getTempo(),String.join(", ",trackList));
    }
}
