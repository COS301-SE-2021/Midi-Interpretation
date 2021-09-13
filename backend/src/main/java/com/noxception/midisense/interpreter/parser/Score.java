package com.noxception.midisense.interpreter.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noxception.midisense.interpreter.broker.JSONUtils;
import lombok.SneakyThrows;

import java.util.List;

public class Score {

    @JsonProperty("key_signatures")
    public List<KeySignature> KeySignatureMap;

    @JsonProperty("time_signatures")
    public List<TimeSignature> TimeSignatureMap;

    @JsonProperty("tempos")
    public List<TempoIndication> TempoIndicationMap;

    @JsonProperty("channel_list")
    public List<Channel> channelList;

    @SneakyThrows
    @Override
    public String toString() {
        return JSONUtils.ObjectToJSON(this);

    }
}
