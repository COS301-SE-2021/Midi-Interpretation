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

import java.io.FileWriter;
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

    @Test
    @DisplayName("Tests parsing Staccato with a valid file, should return an xml tree")
    @Tag(TestTags.VALID_INPUT)
    public void testParseStaccatoValidFile() throws Exception{
        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ParseStaccatoResponse res = interpreterService.parseStaccato(req);
        log(res.getStaccatoSequence());
    }

    @Test
    @DisplayName("Tests parsing Staccato with a invalid file, should return an xml tree")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testParseStaccatoInvalidFile() throws Exception{
        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseStaccato(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests parsing Staccato with a empty file, should return an xml tree")
    @Tag(TestTags.EMPTY_INPUT)
    public void testParseStaccatoEmptyFile() throws Exception{
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseStaccato(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }




    @Test
    @DisplayName("Tests parsing JSON with a valid file, should return a JSON tree")
    @Tag(TestTags.VALID_INPUT)
    public void testParseJSONValidFile() throws Exception{
        ParseJSONRequest req = new ParseJSONRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ParseJSONResponse res = interpreterService.parseJSON(req);
        FileWriter myWriter = new FileWriter("savedContent.txt");
        myWriter.write(res.getParsedScore().toString());
        myWriter.close();
    }


    @Test
    @DisplayName("Tests parsing JSON with an invalid file, should return a JSON tree")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testParseJSONInvalidFile() throws Exception{
        ParseJSONRequest req = new ParseJSONRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseJSON(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests parsing JSON with an empty file, should return a JSON tree")
    @Tag(TestTags.EMPTY_INPUT)
    public void testParseJSONEmptyFile() throws Exception{
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseJSON(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }



    //TODO: DONT RUN THIS TEST - IT WILL DELETE THE ONE GOOD MIDI - IF YOU DO RUN IT, UNDO THE CHANGE IN GITHUB DESKTOP
    @Test
    @DisplayName("Tests processing with a valid file, should return true")
    @Tag(TestTags.VALID_INPUT)
    public void testProcessFileValidFile() throws Exception{
        ProcessFileRequest request = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ProcessFileResponse response = interpreterService.processFile(request);
        log(response.getMessage());
        assertEquals(true,response.getSuccess());
    }

    @Test
    @DisplayName("Tests processing with an invalid file, should return true")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testProcessFileInvalidFile() throws Exception {
        ProcessFileRequest req = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        ProcessFileResponse response = interpreterService.processFile(req);
        log(response.getMessage());
        assertEquals(false, response.getSuccess());
        assertEquals(MIDISenseConfig.FILE_DOES_NOT_EXIST, response.getMessage());
    }

    @Test
    @DisplayName("Tests processing with an empty file, should return true")
    @Tag(TestTags.EMPTY_INPUT)
    public void testProcessFileEmptyFile() throws Exception{
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.processFile(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }





}
