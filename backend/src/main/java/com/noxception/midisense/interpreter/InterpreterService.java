package com.noxception.midisense.interpreter;

import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;

public interface InterpreterService {
    UploadFileResponse uploadFile(UploadFileRequest request) throws InvalidUploadException;
    InterpretMetreResponse interpretMetre(InterpretMetreRequest request) throws InvalidDesignatorException;
    InterpretTempoResponse interpretTempo(InterpretTempoRequest request) throws InvalidDesignatorException;
    InterpretKeySignatureResponse interpretKeySignature(InterpretKeySignatureRequest request) throws InvalidDesignatorException;
}
