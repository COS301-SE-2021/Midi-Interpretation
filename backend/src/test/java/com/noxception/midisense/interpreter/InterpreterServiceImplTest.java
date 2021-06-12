package com.noxception.midisense.interpreter;

import com.noxception.midisense.MIDISenseUnitTest;
import com.noxception.midisense.TestingDictionary;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterServiceImplTest extends MIDISenseUnitTest {

    private InterpreterServiceImpl interpreterService;

    @BeforeEach
    public void setUp() {
        interpreterService = new InterpreterServiceImpl();
    }

    @Test
    @DisplayName("Tests uploading with a valid file byte array, should store in MIDIPool.")
    @Tag(TestTags.VALID_INPUT)
    public void testUploadFileValidFile() throws InvalidUploadException{
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        UploadFileResponse res = interpreterService.uploadFile(req);
        assertEquals(res.getFileDesignator().getClass(), UUID.class);
        assertNotEquals(res.getFileDesignator(), null);
        log(res.getFileDesignator());
    }

    @Test
    @DisplayName("Tests uploading with an invalid file byte array, should throw exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testUploadFileInvalidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                ()->interpreterService.uploadFile(req),
                MIDISenseConfig.EMPTY_FILE_EXCEPTION_TEXT);
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_FILE_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests uploading with an empty request object, should throw exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testUploadFileEmptyRequest(){
        UploadFileRequest req = null;
        try {
            UploadFileResponse res = interpreterService.uploadFile(req);
        } catch (InvalidUploadException e) {

            InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                    ()->interpreterService.uploadFile(req),
                    MIDISenseConfig.EMPTY_FILE_EXCEPTION_TEXT);
            assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_FILE_EXCEPTION_TEXT));
        }
    }

    @Test
    @DisplayName("Tests interpreting metre with a valid file designator, should return a valid metre object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretMetreValidFile(){
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretMetreResponse res = null;
        try {
            res = interpreterService.interpretMetre(req);
            log(res.getMetre());
            assertNotEquals(res.getMetre(),null);
        } catch (InvalidDesignatorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Tests interpreting tempo with a valid file designator, should return a valid tempo object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretTempoValidFile(){
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretTempoResponse res = null;
        try {
            res = interpreterService.interpretTempo(req);
            log(res.getTempo());
            assertNotEquals(res.getTempo(),null);
        } catch (InvalidDesignatorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Tests interpreting key signature with a valid file designator, should return a valid key signature object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretKeySignatureValidFile(){
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretKeySignatureResponse res = null;
        try {
            res = interpreterService.interpretKeySignature(req);
            log(res.getKeySignature());
            assertNotEquals(res.getKeySignature(),null);
        } catch (InvalidDesignatorException | InvalidKeySignatureException e) {
            e.printStackTrace();
        }
    }

    //TODO: CLAUDIO: test Key sig with invalid file and empty request

    @Test
    @DisplayName("Tests interpreting metre with an invalid file designator, should throw an exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testInterpretMetreInvalidFile(){
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(req),
                MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting tempo with an invalid file designator, should throw an exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testInterpretTempoInvalidFile(){
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT);
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testInterpretMetreEmptyRequest(){
        InterpretMetreRequest req = null;
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(req),
                MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testInterpretTempoEmptyRequest(){
        InterpretTempoRequest req = null;
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }

    //TODO: ADRIAN: TEST PARSE STACCATO FOR VALID, INVALID AND EMPTY



}
