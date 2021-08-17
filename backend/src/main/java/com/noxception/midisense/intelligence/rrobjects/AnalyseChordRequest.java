package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.RequestObject;
import javassist.bytecode.ByteArray;

import java.util.UUID;

public class AnalyseChordRequest extends RequestObject {
    /**
     * ATTRIBUTE
     */
    private final byte[] compound;//note and octave range stored in string

    /**
     * CONSTRUCTOR
     * @param ss a byte array containing the note and octave range
     */
    public AnalyseChordRequest(byte[] ss) {
        this.compound =ss;
    }

    /**
     * GET method
     * @return compound
     */
    public byte[] getCompound() {
        return compound;
    }
}
