package com.noxception.midisense.intelligence.exceptions;

public class MissingStrategyException extends Exception{
    /**
     * Exception method for identifying missing strategies
     * @param message
     */
    public MissingStrategyException(String message) {
        super(message);
    }
}
