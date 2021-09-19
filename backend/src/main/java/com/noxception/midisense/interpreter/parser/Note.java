package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noxception.midisense.interpreter.broker.JSONUtils;
import lombok.SneakyThrows;

public class Note {

    @JsonProperty("value")
    public int value;

    @JsonProperty("on_velocity")
    public int onVelocity;

    @JsonProperty("off_velocity")
    public int offVelocity;

    @JsonProperty("duration")
    public int duration;

    @SneakyThrows
    @Override
    public String toString() {
        return JSONUtils.ObjectToJSON(this);

    }
}
