package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noxception.midisense.interpreter.broker.JSONUtils;
import lombok.SneakyThrows;

import java.util.List;

public class Channel {
    @JsonProperty("channel")
    public int channelNumber;

    @JsonProperty("instrument")
    public String instrument;

    @JsonProperty("ticks_per_beat")
    public int ticksPerBeat;

    @JsonProperty("track")
    public List<Track> trackMap;

    @SneakyThrows
    @Override
    public String toString() {
        return JSONUtils.ObjectToJSON(this);

    }
}
