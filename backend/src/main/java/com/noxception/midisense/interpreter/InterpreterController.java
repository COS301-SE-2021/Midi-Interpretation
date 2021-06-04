package com.noxception.midisense.interpreter;

import com.noxception.midisense.api.InterpreterApi;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import com.noxception.midisense.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class InterpreterController implements InterpreterApi {

    @Autowired
    InterpreterServiceImpl interpreterService;


    @Override
    public ResponseEntity<InterpreterUploadFileResponse> uploadFile(InterpreterUploadFileRequest body) {
        InterpreterUploadFileResponse responseObject = new InterpreterUploadFileResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UploadFileRequest req = new UploadFileRequest(intArrayToByteArray(body.getFileContents()));
            UploadFileResponse res = interpreterService.uploadFile(req);
            responseObject.setFileDesignator(res.getFileDesignator().toString());
        } catch (InvalidUploadException e) {
            returnStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    @Override
    public ResponseEntity<InterpreterInterpretMetreResponse> interpretMetre(InterpreterInterpretMetreRequest body) {
        InterpreterInterpretMetreResponse responseObject = new InterpreterInterpretMetreResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(body.getFileDesignator()));
            InterpretMetreResponse res = interpreterService.interpretMetre(req);

            InterpreterInterpretMetreResponseTimeSignature time = new InterpreterInterpretMetreResponseTimeSignature();
            time.setBeatValue(res.getMetre().getBeatValue());
            time.setNumBeats(res.getMetre().getNumBeats());
            responseObject.setTimeSignature(time);
        } catch (InvalidDesignatorException | IllegalArgumentException e){
            returnStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    @Override
    public ResponseEntity<InterpreterInterpretTempoResponse> interpretTempo(InterpreterInterpretTempoRequest body) {
        InterpreterInterpretTempoResponse responseObject = new InterpreterInterpretTempoResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(body.getFileDesignator()));
            InterpretTempoResponse res = interpreterService.interpretTempo(req);
            responseObject.setTempoIndication(res.getTempo().getTempo());
        } catch (InvalidDesignatorException | IllegalArgumentException e){
            returnStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    // HELPER METHODS
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
