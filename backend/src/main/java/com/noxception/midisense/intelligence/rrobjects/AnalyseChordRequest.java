package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;

import java.util.UUID;

public class AnalyseChordRequest extends RequestObject {

    private final String[] s;

    public AnalyseChordRequest(String[] ss) {
        this.s =ss;
    }
}
