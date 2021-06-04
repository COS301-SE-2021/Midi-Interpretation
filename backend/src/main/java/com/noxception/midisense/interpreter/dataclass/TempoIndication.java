package com.noxception.midisense.interpreter.dataclass;

public class TempoIndication {
    private int tempo;

    public TempoIndication(int tempo) {
        this.tempo = tempo;
    }

    public int getTempo() {
        return tempo;
    }

    @Override
    public String toString() {
        return tempo+" bpm";
    }
}
