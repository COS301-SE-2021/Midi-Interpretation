package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noxception.midisense.interpreter.broker.JSONUtils;
import lombok.SneakyThrows;

import java.util.List;

public class Track {
    @JsonProperty("tick")
    public int tick;

    @JsonProperty("notes")
    public List<Note> notes;

    @SneakyThrows
    @Override
    public String toString() {
        return JSONUtils.ObjectToJSON(this);

    }

}
