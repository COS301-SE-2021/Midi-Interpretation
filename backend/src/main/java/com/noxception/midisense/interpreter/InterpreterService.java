package com.noxception.midisense.interpreter;

import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;

import javax.sound.midi.InvalidMidiDataException;

public interface InterpreterService {

    //================================
    // FRONT END USE CASES
    //================================

    UploadFileResponse uploadFile(UploadFileRequest request) throws InvalidUploadException;
    ProcessFileResponse processFile(ProcessFileRequest request) throws InvalidDesignatorException;

    //================================
    // AUX USE CASES
    //================================

    InterpretMetreResponse interpretMetre(InterpretMetreRequest request) throws InvalidDesignatorException;
    InterpretTempoResponse interpretTempo(InterpretTempoRequest request) throws InvalidDesignatorException;
    InterpretKeySignatureResponse interpretKeySignature(InterpretKeySignatureRequest request) throws InvalidDesignatorException, InvalidKeySignatureException;
    ParseStaccatoResponse parseStaccato(ParseStaccatoRequest request) throws InvalidDesignatorException;
    ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidMidiDataException;
}
