package com.noxception.midisense.interpreter.parser;

import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Class that represents an abstraction of the musical elements of a piece, as extracted from a midi file during parsing.
 *  The class provides methods that allow for a high-level interaction with musical elements and not low-level sequence data of a midi file
 *
 * For more information on parsing, see {@link MIDISenseParserListener}
 * @author Adrian Rae
 * @since 1.0.0
 */
public class Score {

    private final HashMap<Integer,Track> trackMap = new HashMap<>();
    private KeySignature keySignature;
    private TimeSignature timeSignature;
    private TempoIndication tempoIndication;

    public Score() {
        this.keySignature = new KeySignature();
        this.timeSignature = new TimeSignature(4,4);
        this.tempoIndication = new TempoIndication(0);
    }

    /** Associates a new track with the score, with the given index
     *
     * @param trackNumber the index of a track in the score
     */
    public void addTrack(int trackNumber){
        trackMap.put(trackNumber,new Track());
    }

    /** Associates a new track with the score, with the given index
     *
     * @param trackNumber the index of a track in the score
     */
    public void addTrack(int trackNumber, String trackData){
        trackMap.put(trackNumber,new Track(trackNumber,trackData));
    }


    /** Associates a key signature to the score
     *
     * @param x the tonal center of the key
     * @param y the key signature relative to number of accidentals
     */
    public void setKeySignature(byte x, byte y){
        this.keySignature = new KeySignature((int) x, ((int) y == 1));
    }

    /** Associates a key signature to the score
     *
     * @param x the beat value of the signature
     * @param y the beat duration of the signature
     */
    public void setTimeSignature(byte x, byte y){
        this.timeSignature = new TimeSignature((int) x, (int) Math.pow(2,(int) y));
    }

    /** Associates a tempo indication to the score
     *
     * @param tempoIndication the tempo
     */
    public void setTempoIndication(int tempoIndication){
        this.tempoIndication = new TempoIndication(tempoIndication);
    }


    /** Returns a track corresponding to a certain index
     *
     * @param trackNumber the index of the track in the score
     * @return the track corresponding to the index, or null if none exists
     */
    public Track getTrack(int trackNumber){
        return trackMap.get(trackNumber);
    }

    /** A method that returns a lookup of tracks and their indices
     *
     * @return a map of track indices to their tracks
     */
    public HashMap<Integer, Track> getTrackMap() {
        return trackMap;
    }

    /**
     * @return the key signature of the score
     */
    public KeySignature getKeySignature() {
        return keySignature;
    }

    /**
     * @return the time signature of the score
     */
    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    /**
     * @return the tempo indication of the score
     */
    public TempoIndication getTempoIndication() {
        return tempoIndication;
    }

    /** A method that returns a high-level, hierarchical representation of a work
     *
     * @return the JSON-serialised representation of the score
     */
    @Override
    public String toString() {
        List<String> trackList = new ArrayList<>();
        for(Track t : trackMap.values()){
            trackList.add(t.toString());
        }
        return String.format("{\"key_signature\": \"%s\", \"time_signature\": \"%s\", \"tempo_indication\": %d, \"tracks\": [%s]}",this.keySignature,this.timeSignature,this.tempoIndication.getTempo(),String.join(", ",trackList));
    }

}
