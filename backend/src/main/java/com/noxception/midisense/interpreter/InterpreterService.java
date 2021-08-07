package com.noxception.midisense.interpreter;

import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.MIDISenseParserListener;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.springframework.stereotype.Service;

import javax.sound.midi.InvalidMidiDataException;

/**
 * Class that is used for uploading a midi file to a specified storage location temporarily.
 * In addition it allows the interpretation of works whose corresponding metadata can be persisted to an external CRUD repository by way of a JPA extension
 *
 * More details on parsing can be found at {@link MIDISenseParserListener}
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
@Service
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
    InterpretKeySignatureResponse interpretKeySignature(InterpretKeySignatureRequest request) throws InvalidDesignatorException;
    ParseStaccatoResponse parseStaccato(ParseStaccatoRequest request) throws InvalidDesignatorException;
    ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidMidiDataException;
}
