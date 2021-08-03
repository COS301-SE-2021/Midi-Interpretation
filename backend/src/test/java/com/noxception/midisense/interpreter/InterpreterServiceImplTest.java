package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.FileWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class InterpreterServiceImplTest extends MIDISenseUnitTest {

    @Autowired
    private InterpreterServiceImpl interpreterService;

    @BeforeEach
    public void setUp() {
        LogType[] monitorList = {LogType.DEBUG};
        this.monitor(monitorList);
    }

    /** ************************************************************************************ */

    /**UploadFile*/
    @Test
    public void test_UploadFile_IfValidFile_ThenAccurateInfo() {

    }
    @Test
    public void test_UploadFile_IfEmptyFile_ThenException() {
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                ()->interpreterService.uploadFile(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }
    @Test
    public void test_UploadFile_IfHugeFile_ThenException() {

    }
    @Test
    public void test_UploadFile_IfNonMidiFile_ThenException() {

    }

    /**ProcessFile*/
    @Test
    public void test_ProcessFile_IfNotInStorage_ThenException() {

    }
    @Test
    public void test_ProcessFile_IfInStorage_ThenAccurate() {

    }
    @Test
    public void test_ProcessFile_IfAlreadyInDatabase_ThenException() {
        ProcessFileRequest req = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.processFile(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_ALREADY_EXISTS_EXCEPTION_TEXT)));

    }

    /**InterpretMetre*/
    @Test
    public void test_InterpretMetre_IfNotInDatabase_ThenException() {
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }
    @Test
    public void test_InterpretMetre_IfInDatabase_ThenAccurate() {

    }
    @Test
    public void test_InterpretMetre_IfEmptyRequest_ThenException() {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    /**InterpretTempo*/
    @Test
    public void test_InterpretTempo_IfNotInDatabase_ThenException() {
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }
    @Test
    public void test_InterpretTempo_IfInDatabase_ThenAccurate() {

    }
    @Test
    public void test_InterpretTempo_IfEmptyRequest_ThenException() {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    /**InterpretKeySignature*/
    @Test
    public void test_InterpretKeySignature_IfNotInDatabase_ThenException() {
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }
    @Test
    public void test_InterpretKeySignature_IfInDatabase_ThenAccurate() {

    }
    @Test
    public void test_InterpretKeySignature_IfEmptyRequest_ThenException() {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    /**ParseJSON*/
    @Test
    public void test_ParseJSON_IfNotInStorage_ThenException() {

    }
    @Test
    public void test_ParseJSON_IfInStorage_ThenAccurate() {

    }

    /** ************************************************************************************ */

    @Ignore
    @Test
    @DisplayName("Tests uploading with a valid file byte array, should store in MIDIPool.")
    public void testUploadFileValidFile() throws InvalidUploadException{
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        UploadFileResponse res = interpreterService.uploadFile(req);
        log(res.getFileDesignator(),LogType.DEBUG);
    }

    @Ignore
    @Test
    @DisplayName("Tests uploading with an invalid file byte array, should throw exception.")
    public void testUploadFileInvalidFile(){
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                ()->interpreterService.uploadFile(req),
                "An empty file should not be saved.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_FILE_EXCEPTION_TEXT)));
    }

    @Ignore
    @Test
    @DisplayName("Tests uploading with an empty request object, should throw exception.")
    public void testUploadFileEmptyRequest(){
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
            ()->interpreterService.uploadFile(null),
            "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));

    }

    @Test
    @DisplayName("Tests interpreting metre with a valid file designator, should return a valid metre object.")
    public void testInterpretMetreValidFile() throws Exception {
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretMetreResponse res = interpreterService.interpretMetre(req);
        log(res.getMetre(),LogType.DEBUG);
    }

    @Test
    @DisplayName("Tests interpreting tempo with a valid file designator, should return a valid tempo object.")
    public void testInterpretTempoValidFile() throws InvalidDesignatorException {
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretTempoResponse res = interpreterService.interpretTempo(req);
        log(res.getTempo(),LogType.DEBUG);
    }

    @Test
    @DisplayName("Tests interpreting key signature with a valid file designator, should return a valid key signature object.")
    public void testInterpretKeySignatureValidFile() throws InvalidDesignatorException {
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretKeySignatureResponse res = interpreterService.interpretKeySignature(req);
        log(res.getKeySignature(),LogType.DEBUG);
        System.out.println(res.getKeySignature());
    }

    @Test
    @DisplayName("Tests interpreting metre with an invalid file designator, should throw an exception.")
    public void testInterpretMetreInvalidFile(){
        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests interpreting tempo with an invalid file designator, should throw an exception.")
    public void testInterpretTempoInvalidFile(){
        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }
    @Test
    @DisplayName("Tests interpreting Key Signature with an invalid file designator, should throw an exception.")
    public void testInterpretKeySignatureInvalidFile(){
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
    public void testInterpretMetreEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretMetre(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
    public void testInterpretTempoEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretTempo(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests interpreting Key Signature with an empty request, should throw an exception.")
    public void testInterpretKeySignatureEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests parsing Staccato with a valid file, should return an xml tree")
    public void testParseStaccatoValidFile() throws Exception{
        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ParseStaccatoResponse res = interpreterService.parseStaccato(req);
        log(res.getStaccatoSequence(),LogType.DEBUG);
    }

    @Test
    @DisplayName("Tests parsing Staccato with a invalid file, should return an xml tree")
    public void testParseStaccatoInvalidFile() throws Exception{
        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseStaccato(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests parsing Staccato with a empty file, should return an xml tree")
    public void testParseStaccatoEmptyFile() throws Exception{
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseStaccato(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }




    @Test
    @DisplayName("Tests parsing JSON with a valid file, should return a JSON tree")
    public void testParseJSONValidFile() throws Exception{
        ParseJSONRequest req = new ParseJSONRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ParseJSONResponse res = interpreterService.parseJSON(req);
        FileWriter myWriter = new FileWriter("ParseJSONSuccess.txt");
        myWriter.write(res.getParsedScore().toString());
        myWriter.close();
    }


    @Test
    @DisplayName("Tests parsing JSON with an invalid file, should return a JSON tree")
    public void testParseJSONInvalidFile() throws Exception{
        ParseJSONRequest req = new ParseJSONRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseJSON(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_SYSTEM_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests parsing JSON with an empty file, should return a JSON tree")
    public void testParseJSONEmptyFile() throws Exception{
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.parseJSON(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }



    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing with a valid file, should return true")
    public void testProcessFileValidFile() throws Exception{
        ProcessFileRequest request = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        ProcessFileResponse response = interpreterService.processFile(request);
        log(response.getMessage(),LogType.DEBUG);
        assertEquals(true,response.getSuccess());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing with an invalid file, should return true")
    public void testProcessFileInvalidFile() throws Exception {
        ProcessFileRequest req = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        ProcessFileResponse response = interpreterService.processFile(req);
        log(response.getMessage(),LogType.DEBUG);
        assertEquals(false, response.getSuccess());
        assertEquals(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT), response.getMessage());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing with an empty file, should return true")
    public void testProcessFileEmptyFile() throws Exception{
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.processFile(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }





}
