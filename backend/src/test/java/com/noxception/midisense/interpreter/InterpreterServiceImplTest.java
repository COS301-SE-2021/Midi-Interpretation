package com.noxception.midisense.interpreter;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
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
                "An empty file should not be saved.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_FILE_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests uploading with an empty request object, should throw exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testUploadFileEmptyRequest(){
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
            ()->interpreterService.uploadFile(null),
            "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));

    }

    @Test
    @DisplayName("Tests interpreting metre with a valid file designator, should return a valid metre object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretMetreValidFile() throws Exception {
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretMetreResponse res = interpreterService.interpretMetre(req);
        log(res.getMetre());
    }

    @Test
    @DisplayName("Tests interpreting tempo with a valid file designator, should return a valid tempo object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretTempoValidFile() throws InvalidDesignatorException {
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretTempoResponse res = interpreterService.interpretTempo(req);
        log(res.getTempo());
    }

    @Test
    @DisplayName("Tests interpreting key signature with a valid file designator, should return a valid key signature object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretKeySignatureValidFile() throws InvalidDesignatorException, InvalidKeySignatureException {
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretKeySignatureResponse res = interpreterService.interpretKeySignature(req);
        log(res.getKeySignature());
    }

    @Test
    @DisplayName("Tests interpreting metre with an invalid file designator, should throw an exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testInterpretMetreInvalidFile(){
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting tempo with an invalid file designator, should throw an exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testInterpretTempoInvalidFile(){
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }
    @Test
    @DisplayName("Tests interpreting Key Signature with an invalid file designator, should throw an exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testInterpretKeySignatureInvalidFile(){
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testInterpretMetreEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testInterpretTempoEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting Key Signature with an empty request, should throw an exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testInterpretKeySignatureEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }

    //TODO: ADRIAN: TEST PARSE STACCATO FOR VALID, INVALID AND EMPTY

    @Test
    @DisplayName("Tests parsing Staccato with a valid file, should return an xml tree")
    @Tag(TestTags.VALID_INPUT)
    public void testParseStacatoValidFile() throws Exception{
        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ParseStaccatoResponse res = interpreterService.parseStaccato(req);
        log(res.getStaccatoSequence());
    }

    @Test
    @DisplayName("Tests parsing Staccato with a invalid file, should return an xml tree")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testParseStacatoInvalidFile() throws Exception{
        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseStaccato(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }



    @Test
    @DisplayName("Tests parsing XML with a valid file, should return an xml tree")
    @Tag(TestTags.VALID_INPUT)
    public void testParseXMLValidFile() throws Exception{
        ParseXMLRequest req = new ParseXMLRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ParseXMLResponse res = interpreterService.parseXML(req);
        log(res.getXMLSequence());
    }



}
