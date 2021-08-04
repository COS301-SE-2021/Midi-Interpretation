package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;
import javassist.bytecode.ByteArray;

import java.util.UUID;

public class AnalyseChordRequest extends RequestObject {

    private final byte[] compound;//note and octave range stored in string

    public AnalyseChordRequest(byte[] ss) {
        this.compound =ss;
    }

    public byte[] getCompound() {
        return compound;
    }
}
