package com.noxception.midisense.intelligence.dataclass;

import java.util.HashSet;
import java.util.Set;

public enum ChordType {
    OPEN_FIFTH (new byte[]{0,7}, "5"),
    MAJOR (new byte[]{0,4,7}, "Maj"),
    MINOR (new byte[]{0,3,7}, "Min"),
    DIMINISHED (new byte[]{0,3,6}, "Dim"),
    AUGMENTED (new byte[]{0,4,8}, "Aug"),
    SUSPENDED_FOURTH (new byte[]{0,5,7}, "Sus4"),
    SUSPENDED_SECOND (new byte[]{0,2,7}, "Sus2"),
    DOMINANT_SEVENTH (new byte[]{0,4,7,10}, "7"),
    MAJOR_SEVENTH (new byte[]{0,4,7,11}, "Maj7"),
    MINOR_SEVENTH (new byte[]{0,3,7,10}, "Min7"),
    DIMINISHED_SEVENTH (new byte[]{0,3,6,9}, "Dim7"),
    ALTERED (new byte[]{}, "Altered");

    private final Set<Byte> byteMask;
    private final String shortName;

    ChordType(byte[] mask, String shortName) {
        this.shortName = shortName;
        this.byteMask = new HashSet<Byte>();
        for(byte b: mask)
            this.byteMask.add(b);
    }

    public Set<Byte> getByteMask(){
        return this.byteMask;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }
}
