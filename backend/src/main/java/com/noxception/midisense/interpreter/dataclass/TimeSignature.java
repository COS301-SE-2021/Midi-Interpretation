package com.noxception.midisense.interpreter.dataclass;

public class TimeSignature {
    private int numBeats;
    private int beatValue;

    public TimeSignature(int numBeats, int beatValue) {
        this.numBeats = numBeats;
        this.beatValue = beatValue;
    }

    public int getNumBeats() {
        return numBeats;
    }

    public int getBeatValue() {
        return beatValue;
    }

    @Override
    public String toString() {
        return numBeats +"/"+beatValue;
    }
}
