package com.noxception.midisense.interpreter;

import com.noxception.midisense.TestingDictionary;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

            InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                    ()->interpreterService.uploadFile(req),
                    "[File System Failure] ");
            assertTrue(thrown.getMessage().contains("[File System Failure] "));
        }
    }

    @Test
    public void testUploadFileEmptyRequest(){
        UploadFileRequest req = null;
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {

            InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                    ()->interpreterService.uploadFile(req),
                    "[Bad Request] No request made");
            assertTrue(thrown.getMessage().contains("[Bad Request] No request made"));
        }


    }

}