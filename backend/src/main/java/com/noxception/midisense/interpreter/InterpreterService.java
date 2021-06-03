package com.noxception.midisense.interpreter;

import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;

public interface InterpreterService {
    public UploadFileResponse uploadFile(UploadFileRequest request) throws InvalidUploadException;
}
