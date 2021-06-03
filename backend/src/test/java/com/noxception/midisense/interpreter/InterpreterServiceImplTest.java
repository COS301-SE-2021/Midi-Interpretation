package com.noxception.midisense.interpreter;

import com.noxception.midisense.TestingDictionary;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.Beans;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class InterpreterServiceImplTest {


    //TODO: @Autowired
    InterpreterServiceImpl interpreterService = new InterpreterServiceImpl();

    @Test
    public void testUploadFileValidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
            assertEquals(res.getFileDesignator().getClass(), UUID.class);
            assertNotEquals(res.getFileDesignator(), null);
        } catch (InvalidUploadException e) {
        }
    }

    @Test
    public void testUploadFileInvalidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                ()->interpreterService.uploadFile(req),
                "[File System Failure] ");
        assertTrue(thrown.getMessage().contains("[File System Failure] "));
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