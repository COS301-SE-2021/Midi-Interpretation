package com.noxception.midisense.interpreter.exceptions;

public class InvalidDesignatorException extends Exception {

    /**
     * Constructs a new exception with the specified detail message informing
     * that an invalid file designator is detected
     *
     * @param message detailed message
     */
    public InvalidDesignatorException(String message){
        super(message);
    }
}
