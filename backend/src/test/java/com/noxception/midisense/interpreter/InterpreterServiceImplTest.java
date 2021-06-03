package com.noxception.midisense.interpreter;

import com.noxception.midisense.TestingDictionary;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.beans.Beans;


class InterpreterServiceImplTest {


    //@Autowired
    InterpreterServiceImpl interpreterService = new InterpreterServiceImpl();

    @Test
    public void testUploadFileValidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {
            // Nothing
        }
        Assertions.assertEquals(req.getFileContents(),validFileContents);
    }

    @Test
    public void testUploadFileInvalidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {
            // SHOULD DO THIS - TODO: CHANGE TO ASSERTTHROWS
           // assertThrows("s");
        }
    }

    @Test
    public void testUploadFileEmptyRequest(){
        UploadFileRequest req = null;
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {
            // SHOULD DO THIS TODO: CHANGE TO ASSERTTHROWS
        }
        //assertThatThrownBy(()->interpreterService.uploadFile(req))
        //        .isInstanceOf(InvalidUploadException.class)
        //        .hasMessageContaining("[Bad Request] No request made");

    }

}