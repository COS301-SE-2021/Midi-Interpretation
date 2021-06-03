package com.noxception.midisense.interpreter;

import com.noxception.midisense.TestingDictionary;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterServiceImplTest {

    //TODO: CLAUDIO - fill in assertions and TestingDictionary

    @Autowired
    InterpreterServiceImpl interpreterService;

    @Test
    public void testUploadFileValidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {
            // SOMETHING
        }
        //ASSERTION FOR VALID UUID RETURNED
    }

    @Test
    public void testUploadFileInvalidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {
            // SHOULD DO THIS
        }
    }

    @Test
    public void testUploadFileEmptyRequest(){
        UploadFileRequest req = null;
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {
            // SHOULD DO THIS
        }
    }

}