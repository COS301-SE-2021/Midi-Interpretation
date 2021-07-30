package com.noxception.midisense.interpreter;

import com.noxception.midisense.api.InterpreterApi;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.display.rrobjects.GetPieceMetadataRequest;
import com.noxception.midisense.display.rrobjects.GetPieceMetadataResponse;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import com.noxception.midisense.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<InterpreterProcessFileResponse> processFile(InterpreterProcessFileRequest body) {
        InterpreterProcessFileResponse responseObject = new InterpreterProcessFileResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
            ProcessFileResponse res = interpreterService.processFile(req);
            responseObject.setMessage(res.getMessage());
            responseObject.setSuccess(res.getSuccess());
        }
        catch (InvalidDesignatorException | IllegalArgumentException e) {
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    @RequestMapping(
            value="/interpreter/uploadFile",
            method= RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<InterpreterUploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        //method body
        InterpreterUploadFileResponse responseObject = new InterpreterUploadFileResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            byte[] fileContents = file.getBytes();
            UploadFileRequest req = new UploadFileRequest(fileContents);
            UploadFileResponse res = interpreterService.uploadFile(req);
            responseObject.setFileDesignator(res.getFileDesignator().toString());
        }
        catch (IllegalArgumentException | InvalidUploadException | IOException e) {
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);

    }

//    @Override
//    public ResponseEntity<InterpreterUploadFileResponse> uploadFile(Object body) {
//        InterpreterUploadFileResponse responseObject = new InterpreterUploadFileResponse();
//        HttpStatus returnStatus = HttpStatus.OK;
//        try{
//            UploadFileRequest req = new UploadFileRequest(body);
//            UploadFileResponse res = interpreterService.uploadFile(req);
//            responseObject.setFileDesignator(res.getFileDesignator().toString());
//        }
//        catch (IllegalArgumentException | InvalidUploadException e) {
//            returnStatus = HttpStatus.BAD_REQUEST;
//            responseObject = null;
//        }
//        return new ResponseEntity<>(responseObject,returnStatus);
//    }

//    @Override
//    public ResponseEntity<InterpreterUploadFileResponse> uploadFile(InterpreterUploadFileRequest body) {
//        InterpreterUploadFileResponse responseObject = new InterpreterUploadFileResponse();
//        HttpStatus returnStatus = HttpStatus.OK;
//        try{
//            List<Integer> intArray = body.getFileContents();
//            byte[] byteArray = intArrayToByteArray(intArray);
//            UploadFileRequest req = new UploadFileRequest(byteArray);
//            UploadFileResponse res = interpreterService.uploadFile(req);
//            responseObject.setFileDesignator(res.getFileDesignator().toString());
//        }
//        catch (IllegalArgumentException | InvalidUploadException e) {
//            returnStatus = HttpStatus.BAD_REQUEST;
//            responseObject = null;
//        }
//        return new ResponseEntity<>(responseObject,returnStatus);
//    }

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
