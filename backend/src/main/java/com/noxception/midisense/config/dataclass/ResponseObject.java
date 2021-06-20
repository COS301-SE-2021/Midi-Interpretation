package com.noxception.midisense.config.dataclass;

import java.util.Date;
import java.util.UUID;

public class ResponseObject {
    private final UUID designator = UUID.randomUUID();
    private final Date timestamp = new Date();

    public UUID getDesignator() {
        return designator;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
