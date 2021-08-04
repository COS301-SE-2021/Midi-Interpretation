package com.noxception.midisense.intelligence.dataclass;

public class GenrePredication {
    private final String genreName;
    private final double certainty;

    public GenrePredication(String genreName, double certainty) {
        this.genreName = genreName;
        this.certainty = certainty;
    }

    public String getGenreName() {
        return genreName;
    }

    public double getCertainty() {
        return certainty;
    }
}
