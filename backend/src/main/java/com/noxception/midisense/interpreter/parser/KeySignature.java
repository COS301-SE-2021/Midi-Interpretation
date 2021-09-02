package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeySignature {

    @JsonProperty("tick")
    public int tick;

    @JsonProperty("key")
    public String commonName;

}
