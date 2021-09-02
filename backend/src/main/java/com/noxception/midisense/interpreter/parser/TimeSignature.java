package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeSignature {

    @JsonProperty("tick")
    public int tick;

    @JsonProperty("time")
    public InnerTime time;

    public static class InnerTime{

        @JsonProperty("num_beats")
        public int numBeats;

        @JsonProperty("beat_value")
        public int beatValue;
    }

}
