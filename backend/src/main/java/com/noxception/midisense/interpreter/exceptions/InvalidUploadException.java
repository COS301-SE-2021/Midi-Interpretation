package com.noxception.midisense.interpreter.exceptions;

public class InvalidUploadException extends Exception{

    /**
     * Constructs a new exception with the specified detail message informing
     * that an invalid file was uploaded
     *
     * @param message detailed message
     */
    public InvalidUploadException(String message){
        super(message);
    }
}
