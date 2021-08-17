package com.noxception.midisense.intelligence.dataclass;

public class ChordPrediction {
    private final String chordName;
    private final double certainty;

    public ChordPrediction(String chordName, double certainty) {
        this.chordName = chordName;
        this.certainty = certainty;
    }

    public String getChordName() {
        return chordName;
    }

    public double getCertainty() {
        return certainty;
    }

    @Override
    public String toString() {
        return "ChordPredication{" +
                "chordName='" + chordName + '\'' +
                ", certainty=" + certainty +
                '}';
    }
}