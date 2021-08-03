package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.Score;
import com.noxception.midisense.interpreter.parser.Track;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.InvalidMidiDataException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
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
    public void test_UploadFile_IfValidFile_ThenAccurateInfo() throws InvalidUploadException, IOException {

        // Generate Valid File, put it in request
        byte[] validFile = new byte[]{1,2,3,4,5};
        UploadFileRequest req = new UploadFileRequest(validFile);

        // Upload the file and get the designator
        UploadFileResponse res = interpreterService.uploadFile(req);
        UUID fileDesignator = res.getFileDesignator();

        //check the designator is not null
        assertNotNull(fileDesignator);

        //check that the resultant file can be opened : was saved to the right directory
        String filename = MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)
                +fileDesignator
                +MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);
        File newlyCreated = new File(filename);

        //check to see that the file contents are the same
        byte[] newContents = Files.readAllBytes(newlyCreated.toPath());
        assertArrayEquals(newContents,validFile);

        newlyCreated = new File(filename);
        //delete the file
        assertTrue(newlyCreated.delete());
    }

    @Test
    public void test_UploadFile_IfEmptyFile_ThenException() {

        // Generate Empty File, put it in request
        UploadFileRequest req = new UploadFileRequest(new byte[]{});

        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class, //for an empty file
                ()->interpreterService.uploadFile(req), //when uploading
                "An empty file should not be processed"); //because

        // Finally, see that the right message was delivered - EMPTY_FILE_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_FILE_EXCEPTION_TEXT)
        ));
    }

    @Transactional
    @Rollback(value = true)
    @Test
    public void test_UploadFile_IfHugeFile_ThenException() {
        // Generate Big File, put it in request

        //max file size in Kb
        int bigIndex = Integer.parseInt(
                MIDISenseConfig.configuration(
                        MIDISenseConfig.ConfigurationName.MAX_FILE_UPLOAD_SIZE
                )
        );

        //max file size in bytes to be bigger than maximum - 1 larger than specified max
        bigIndex = 1 + (int) ( bigIndex*Math.pow(2,10));
        byte[] bigFile = new byte[bigIndex];

        //fill with 0s
        for(int i=0; i<bigIndex; i++)
            bigFile[i] = 0;

        UploadFileRequest req = new UploadFileRequest(bigFile);

        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class, //for a huge file
                ()->interpreterService.uploadFile(req), //when uploading
                "A file exceeding the maximum size should not be processed"); //because

        // Finally, see that the right message was delivered - FILE_TOO_LARGE_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_TOO_LARGE_EXCEPTION_TEXT)
        ));
    }

    /**ProcessFile*/

    @Transactional
    @Rollback(value = true)
    @Test
    public void test_ProcessFile_IfNonMidiFile_ThenException() throws IOException, InvalidDesignatorException {

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator +MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+testName);
        Path originalPath = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_INVALID_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
        ProcessFileResponse res = interpreterService.processFile(req);

        //check that the result is not permitted
        assertEquals(res.getSuccess(),false);
        assertEquals(res.getMessage(),MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));

        //delete the temporary file
        assertTrue(new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+testName).delete());
    }

    @Transactional
    @Rollback(value = true)
    @Test
    public void test_ProcessFile_IfNotInStorage_ThenException() {

        //Create a fake designator
        UUID fileDesignator = UUID.randomUUID();

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that does not exist
                ()->interpreterService.processFile(req), //when processing
                "A file that does not exist cannot be interpreted"); //because

        // Finally, see that the right message was delivered - FILE_TOO_LARGE_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));


    }

    @Transactional
    @Rollback(value = true)
    @Test
    public void test_ProcessFile_IfInStorage_ThenAccurate() throws IOException, InvalidDesignatorException {
        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator +MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+testName);
        Path originalPath = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
        ProcessFileResponse res = interpreterService.processFile(req);

        //Check that the processing is successful
        assertEquals(res.getSuccess(),true);
        assertEquals(res.getMessage(),MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.SUCCESSFUL_PARSING_TEXT));

        //delete the temporary file
        assertTrue(new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+testName).delete());

    }

    @Transactional
    @Rollback(value = true)
    @Test
    public void test_ProcessFile_IfAlreadyInDatabase_ThenException() {
        //TODO: MAKE SURE THE DESIGNATOR PASSED IN IS ALREADY IN DATABASE

        //Get a designator corresponding to a score in the database - whether or not it actually exists
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //make the request
        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that already exists in DB
                ()->interpreterService.processFile(req), //when processing
                "A file that already exists in the database cannot be interpreted"); //because

        // Finally, see that the right message was delivered - FILE_ALREADY_EXISTS_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_ALREADY_EXISTS_EXCEPTION_TEXT)
        ));
    }

    /**InterpretMetre*/
    @Test
    public void test_InterpretMetre_IfNotInDatabase_ThenException() {

        //Create a fake designator
        UUID fileDesignator = UUID.randomUUID();

        //make the request
        InterpretMetreRequest req = new InterpretMetreRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that hasn't been interpreted
                ()->interpreterService.interpretMetre(req), //when interpreting metre
                "No processing should happen if a file doesn't exist." //because
        );

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));
    }

    @Test
    public void test_InterpretMetre_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {

        //Get a designator corresponding to a score in the database - whether or not it actually exists
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //make a request
        InterpretMetreRequest req = new InterpretMetreRequest(fileDesignator);
        InterpretMetreResponse res = interpreterService.interpretMetre(req);

        //TODO: ASK ANDREW ABOUT SPECIFIC VALUES VERSUS CONCRETE SATISFACTION CONDITIONS

        //check that the beat value is a positive power of two
        int beatValue = res.getMetre().getBeatValue();
        double c = Math.log(beatValue)/Math.log(2);
        assertEquals(c,Math.floor(c));

        //check that the number of beats is positive
        int numBeats = res.getMetre().getNumBeats();
        assertTrue(numBeats>0);
    }

    @Test
    public void test_InterpretMetre_IfEmptyRequest_ThenException() {
        //Check that the right error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a null request
                ()->interpreterService.interpretMetre(null), //when interpreting metre
                "A null request should not be processed."); //because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
        )));
    }


    /**InterpretTempo*/
    @Test
    public void test_InterpretTempo_IfNotInDatabase_ThenException() {

        //Create a fake designator
        UUID fileDesignator = UUID.randomUUID();

        //make the request
        InterpretTempoRequest req = new InterpretTempoRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that hasn't been interpreted
                ()->interpreterService.interpretTempo(req), //when interpreting tempo
                "No processing should happen if a file doesn't exist." //because
        );

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));

    }
    @Test
    public void test_InterpretTempo_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {

        //Get a designator corresponding to a score in the database - whether or not it actually exists
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //make a request
        InterpretTempoRequest req = new InterpretTempoRequest(fileDesignator);
        InterpretTempoResponse res = interpreterService.interpretTempo(req);

        //see that the tempo is a positive integer
        TempoIndication t = res.getTempo();
        assertTrue(t.getTempo()>0);
    }

    @Test
    public void test_InterpretTempo_IfEmptyRequest_ThenException() {
        //Check that the right error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a null request
                ()->interpreterService.interpretTempo(null), //when interpreting Tempo
                "A null request should not be processed."); //because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
        )));
    }


    /**InterpretKeySignature*/
    @Test
    public void test_InterpretKeySignature_IfNotInDatabase_ThenException() {
        
        //Create a fake designator
        UUID fileDesignator = UUID.randomUUID();

        //make the request
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that hasn't been interpreted
                ()->interpreterService.interpretKeySignature(req), //when interpreting KeySignature
                "No processing should happen if a file doesn't exist." //because
        );

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));
    }

    @Test
    public void test_InterpretKeySignature_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {

        //Get a designator corresponding to a score in the database - whether or not it actually exists
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //make a request
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(fileDesignator);
        InterpretKeySignatureResponse res = interpreterService.interpretKeySignature(req);

        //Check that the key is valid
        String[] keyArray = {"Cbmaj", "Gbmaj", "Dbmaj", "Abmaj", "Ebmaj", "Bbmaj", "Fmaj", "Cmaj", "Gmaj", "Dmaj", "Amaj", "Emaj", "Bmaj", "F#maj", "C#maj", "Abmin", "Ebmin", "Bbmin", "Fmin", "Cmin", "Gmin", "Dmin", "Amin", "Emin", "Bmin", "F#min", "C#min", "G#min", "D#min", "A#min"};
        boolean b = Arrays.asList(keyArray).contains(res.getKeySignature().getSignatureName());
        assertTrue(b);
    }

    @Test
    public void test_InterpretKeySignature_IfEmptyRequest_ThenException() {
        //Check that the right error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a null request
                ()->interpreterService.interpretKeySignature(null), //when interpreting KeySignature
                "A null request should not be processed."); //because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
        )));
    }


    /**ParseJSON*/
    @Test
    public void test_ParseJSON_IfNotInStorage_ThenException(){

        // Generate a new UUID - is unique and so is different from all existing in storage
        UUID fileDesignator = UUID.randomUUID();

        // Make a request with the new designator
        ParseJSONRequest req = new ParseJSONRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for invalid designator - no file
                ()->interpreterService.parseJSON(req), //when parsing
                "No file with this designator should exists in storage" //because
        );

        // Finally, see that the right message was delivered - File does not exist
        assertTrue(thrown.getMessage().contains(
                MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));

    }

    @Transactional
    @Rollback(value = true)
    @Test
    public void test_ParseJSON_IfInStorage_ThenAccurate() throws Exception {

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator +MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+testName);
        Path originalPath = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        //interpret the work and get the score
        ParseJSONRequest req = new ParseJSONRequest(fileDesignator);
        ParseJSONResponse res = interpreterService.parseJSON(req);
        Score score = res.getParsedScore();

        //delete the temporary file
        assertTrue(new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)+testName).delete());

        //1.1 checking size
        Map<Integer, Track> trackMap = score.getTrackMap();
        assertTrue(trackMap.keySet().size()<=16);

        //1.2 key sig
        String[] keyArray = {"Cbmaj", "Gbmaj", "Dbmaj", "Abmaj", "Ebmaj", "Bbmaj", "Fmaj", "Cmaj", "Gmaj", "Dmaj", "Amaj", "Emaj", "Bmaj", "F#maj", "C#maj", "Abmin", "Ebmin", "Bbmin", "Fmin", "Cmin", "Gmin", "Dmin", "Amin", "Emin", "Bmin", "F#min", "C#min", "G#min", "D#min", "A#min"};
        boolean b = Arrays.asList(keyArray).contains(score.getKeySignature().getSignatureName());
        assertTrue(b);

        //1.3 tempo
        assertTrue(score.getTempoIndication().getTempo()>0);


        //1.4 beat val for time sig
        int beatValue = score.getTimeSignature().getBeatValue();
        double c = Math.log(beatValue)/Math.log(2);
        assertEquals(c,Math.floor(c));


        //1.5 beat num for time sig
        int numBeats = score.getTimeSignature().getNumBeats();
        assertTrue(numBeats>0);


        for (Track t: trackMap.values()){
            assertNotEquals(t.getInstrumentString(),"");
            assertTrue(t.getNoteSequence().size()>0);
        }

    }

    /** ************************************************************************************ */

//    @Ignore
//    @Test
//    @DisplayName("Tests uploading with a valid file byte array, should store in MIDIPool.")
//    public void testUploadFileValidFile() throws InvalidUploadException{
//        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
//        UploadFileRequest req = new UploadFileRequest(validFileContents);
//        UploadFileResponse res = interpreterService.uploadFile(req);
//        log(res.getFileDesignator(),LogType.DEBUG);
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests uploading with an invalid file byte array, should throw exception.")
//    public void testUploadFileInvalidFile(){
//        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_invalidFileContents;
//        UploadFileRequest req = new UploadFileRequest(validFileContents);
//        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
//                ()->interpreterService.uploadFile(req),
//                "An empty file should not be saved.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_FILE_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests uploading with an empty request object, should throw exception.")
//    public void testUploadFileEmptyRequest(){
//        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
//            ()->interpreterService.uploadFile(null),
//            "A null request should not be processed.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting metre with a valid file designator, should return a valid metre object.")
//    public void testInterpretMetreValidFile() throws Exception {
//        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
//        InterpretMetreResponse res = interpreterService.interpretMetre(req);
//        log(res.getMetre(),LogType.DEBUG);
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting tempo with a valid file designator, should return a valid tempo object.")
//    public void testInterpretTempoValidFile() throws InvalidDesignatorException {
//        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
//        InterpretTempoResponse res = interpreterService.interpretTempo(req);
//        log(res.getTempo(),LogType.DEBUG);
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting key signature with a valid file designator, should return a valid key signature object.")
//    public void testInterpretKeySignatureValidFile() throws InvalidDesignatorException {
//        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
//        InterpretKeySignatureResponse res = interpreterService.interpretKeySignature(req);
//        log(res.getKeySignature(),LogType.DEBUG);
//        System.out.println(res.getKeySignature());
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting metre with an invalid file designator, should throw an exception.")
//    public void testInterpretMetreInvalidFile(){
//        InterpretMetreRequest req = new InterpretMetreRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.interpretMetre(req),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting tempo with an invalid file designator, should throw an exception.")
//    public void testInterpretTempoInvalidFile(){
//        InterpretTempoRequest req = new InterpretTempoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.interpretTempo(req),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
//    }
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting Key Signature with an invalid file designator, should throw an exception.")
//    public void testInterpretKeySignatureInvalidFile(){
//        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.interpretKeySignature(req),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
//    public void testInterpretMetreEmptyRequest(){
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.interpretMetre(null),
//                "A null request should not be processed.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting metre with an empty request, should throw an exception.")
//    public void testInterpretTempoEmptyRequest(){
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.interpretTempo(null),
//                "A null request should not be processed.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests interpreting Key Signature with an empty request, should throw an exception.")
//    public void testInterpretKeySignatureEmptyRequest(){
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.interpretKeySignature(null),
//                "A null request should not be processed.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests parsing Staccato with a valid file, should return an xml tree")
//    public void testParseStaccatoValidFile() throws Exception{
//        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
//        ParseStaccatoResponse res = interpreterService.parseStaccato(req);
//        log(res.getStaccatoSequence(),LogType.DEBUG);
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests parsing Staccato with a invalid file, should return an xml tree")
//    public void testParseStaccatoInvalidFile() throws Exception{
//        ParseStaccatoRequest req = new ParseStaccatoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.parseStaccato(req),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests parsing Staccato with a empty file, should return an xml tree")
//    public void testParseStaccatoEmptyFile() throws Exception{
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.parseStaccato(null),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//    }
//
//
//
//
//    @Ignore
//    @Test
//    @DisplayName("Tests parsing JSON with a valid file, should return a JSON tree")
//    public void testParseJSONValidFile() throws Exception{
//        ParseJSONRequest req = new ParseJSONRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
//        ParseJSONResponse res = interpreterService.parseJSON(req);
//        FileWriter myWriter = new FileWriter("ParseJSONSuccess.txt");
//        myWriter.write(res.getParsedScore().toString());
//        myWriter.close();
//    }
//
//
//    @Ignore
//    @Test
//    @DisplayName("Tests parsing JSON with an invalid file, should return a JSON tree")
//    public void testParseJSONInvalidFile() throws Exception{
//        ParseJSONRequest req = new ParseJSONRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.parseJSON(req),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_SYSTEM_EXCEPTION_TEXT)));
//    }
//
//    @Ignore
//    @Test
//    @DisplayName("Tests parsing JSON with an empty file, should return a JSON tree")
//    public void testParseJSONEmptyFile() throws Exception{
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.parseJSON(null),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//    }
//
//
//
//    @Ignore
//    @Test
//    @Transactional
//    @Rollback(value = true)
//    @DisplayName("Tests processing with a valid file, should return true")
//    public void testProcessFileValidFile() throws Exception{
//        ProcessFileRequest request = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
//        ProcessFileResponse response = interpreterService.processFile(request);
//        log(response.getMessage(),LogType.DEBUG);
//        assertEquals(true,response.getSuccess());
//    }
//
//    @Ignore
//    @Test
//    @Transactional
//    @Rollback(value = true)
//    @DisplayName("Tests processing with an invalid file, should return true")
//    public void testProcessFileInvalidFile() throws Exception {
//        ProcessFileRequest req = new ProcessFileRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
//        ProcessFileResponse response = interpreterService.processFile(req);
//        log(response.getMessage(),LogType.DEBUG);
//        assertEquals(false, response.getSuccess());
//        assertEquals(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT), response.getMessage());
//    }
//
//    @Ignore
//    @Test
//    @Transactional
//    @Rollback(value = true)
//    @DisplayName("Tests processing with an empty file, should return true")
//    public void testProcessFileEmptyFile() throws Exception{
//        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
//                ()->interpreterService.processFile(null),
//                "No processing should happen if a file doesn't exist.");
//        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
//    }





}
