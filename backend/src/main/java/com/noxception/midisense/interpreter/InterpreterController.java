package com.noxception.midisense.interpreter;

import com.noxception.midisense.api.InterpreterApi;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import com.noxception.midisense.models.InterpreterUploadFileRequest;
import com.noxception.midisense.models.InterpreterUploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

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
