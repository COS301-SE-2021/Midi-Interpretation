package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TempoIndication {

    @JsonProperty("tick")
    public int tick;

    @JsonProperty("tempo")
    public double tempoIndication;


}
