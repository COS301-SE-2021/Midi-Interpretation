package com.noxception.midisense.interpreter;

import com.noxception.midisense.api.InterpreterApi;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import com.noxception.midisense.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin(value = MIDISenseConfig.CROSS_ORIGIN)
@RestController
public class InterpreterController implements InterpreterApi {

    //TODO: WORK ON CONTROLLER

    @Autowired
    InterpreterServiceImpl interpreterService;

    @Override
    public ResponseEntity<InterpreterInterpretMetreResponse> interpretMetre(InterpreterInterpretMetreRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<InterpreterInterpretTempoResponse> interpretTempo(InterpreterInterpretTempoRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<InterpreterParseJSONResponse> parseJSON(InterpreterParseJSONRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<InterpreterParseStaccatoResponse> parseStaccato(InterpreterParseStaccatoRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<InterpreterProcessFileResponse> processFIle(InterpreterProcessFileRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<InterpreterUploadFileResponse> uploadFile(InterpreterUploadFileRequest body) {
        return null;
    }

    // HELPER METHODS BELOW THIS LINE
    //----------------------------------------------------------------------------------------------------
    private byte[] intArrayToByteArray(List<Integer> list)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (int element : list) {
            try {
                out.writeUTF(Integer.toString(element));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

}
