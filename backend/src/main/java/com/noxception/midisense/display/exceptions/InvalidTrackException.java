package com.noxception.midisense.display.exceptions;

public class InvalidTrackException extends Exception{
    /**
     * Exception method for identifying invalid tracks
     * @param message
     */
    public InvalidTrackException(String message) {
        super(message);
    }
}
