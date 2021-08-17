package com.noxception.midisense.interpreter.parser;

/** A class that encapsulates a high-level representation of an individual track sequence (instrument line) of a midi file.
 * The class provides methods that allow for a high-level interaction with musical elements and not low-level sequence data of a midi file
 *
 * For more information on parsing, see {@link MIDISenseParserListener}
 * @author Adrian Rae
 * @since 1.0.0
 */
public class Track {

    private int channel;
    private String trackData;

    public Track() {
    }

    public Track(int channel) {
        this.channel = channel;
    }

    public Track(String trackData) {
        this.trackData = trackData;
    }

    public Track(int channel, String trackData) {
        this.channel = channel;
        this.trackData = trackData;
    }

    public String getTrackData() {
        return trackData;
    }

    public void setTrackData(String trackData) {
        this.trackData = trackData;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return trackData;
    }
}
