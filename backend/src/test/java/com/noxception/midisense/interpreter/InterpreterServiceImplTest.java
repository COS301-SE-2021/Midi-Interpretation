package com.noxception.midisense.interpreter;

import com.noxception.midisense.TestingDictionary;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class InterpreterServiceImplTest {


    @Autowired
    InterpreterServiceImpl interpreterService;

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
                "[Empty File]");
        assertTrue(thrown.getMessage().contains("[Empty File]"));
    }

    @Test
    public void testUploadFileEmptyRequest(){
        UploadFileRequest req = null;
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {

            InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                    ()->interpreterService.uploadFile(req),
                    "[No Request Made]");
            assertTrue(thrown.getMessage().contains("[No Request Made]"));
        }
    }

    @Test
    public void testInterpretMetreValidFile(){
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretMetreResponse res = null;
        try {
            res = interpreterService.interpretMetre(req);
            System.out.println(res.getMetre());
            assertNotEquals(res.getMetre(),null);
        } catch (InvalidDesignatorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInterpretTempoValidFile(){
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretTempoResponse res = null;
        try {
            res = interpreterService.interpretTempo(req);
            System.out.println(res.getTempo());
            assertNotEquals(res.getTempo(),null);
        } catch (InvalidDesignatorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInterpretMetreInvalidFile(){
        /*InterpretMetreRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                "[File System Failure]");
        assertTrue(thrown.getMessage().contains("[File System Failure]"));*/
    }

    @Test
    public void testInterpretTempoInvalidFile(){
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                "[File System Failure]");
        assertTrue(thrown.getMessage().contains("[File System Failure]"));
    }

}