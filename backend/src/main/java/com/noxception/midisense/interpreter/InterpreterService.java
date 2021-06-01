package com.noxception.midisense.interpreter;

import com.noxception.midisense.interpreter.exceptions.InvalidParseException;
import com.noxception.midisense.interpreter.rrobjects.ParseFileRequest;
import com.noxception.midisense.interpreter.rrobjects.ParseFileResponse;

public interface InterpreterService {
    public ParseFileResponse parseFile(ParseFileRequest request) throws InvalidParseException;
}
