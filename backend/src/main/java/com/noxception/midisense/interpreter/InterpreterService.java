package com.noxception.midisense.interpreter;

import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidInterpretationException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.springframework.stereotype.Service;

import javax.sound.midi.InvalidMidiDataException;

/**
 * Class that is used for uploading a midi file to a specified storage location temporarily.
 * In addition it allows the interpretation of works whose corresponding metadata can be persisted to an external CRUD repository by way of a JPA extension
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

    ParseJSONResponse parseJSON(ParseJSONRequest request) throws InvalidDesignatorException, InvalidMidiDataException, InvalidInterpretationException;
}
