package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.dataclass.MockRepository;
import com.noxception.midisense.dataclass.MockRequestBroker;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.*;
import com.noxception.midisense.interpreter.rrobjects.ParseJSONRequest;
import com.noxception.midisense.interpreter.rrobjects.ParseJSONResponse;
import com.noxception.midisense.interpreter.rrobjects.UploadFileRequest;
import com.noxception.midisense.interpreter.rrobjects.UploadFileResponse;
import com.noxception.midisense.repository.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterServiceTest extends MIDISenseUnitTest {

    private InterpreterService interpreterService;
    private StandardConfig configurations;
    private DatabaseManager databaseManager;

    @BeforeEach
    public void mountModule() {
        configurations = new MockConfigurationSettings();
        databaseManager = new MockRepository();
        interpreterService = new InterpreterServiceImpl(databaseManager,configurations);
        ((InterpreterServiceImpl) interpreterService).addDefaultBroker(new MockRequestBroker(configurations));
    }


//    //====================================================================================================================//
//    //                                  BLACK BOX TESTING BELOW                                                           //
//    //====================================================================================================================//
//

    /**UploadFile*/
    /**
     * Description: tests the uploadFile() function by passing in a valid file and saving
     * to the right directory
     * precondition - valid byte stream passed in
     * post condition - valid UUID from the right directory with the sames contents
     */
    @Test
    @DisplayName("Uploading File: input [valid byte stream] expect [valid UUID corresponding to file with same contents]")
    public void test_UploadFile_IfValidFile_ThenAccurateInfo() throws InvalidUploadException, IOException {

        // Generate Valid File, put it in request
        byte[] validFile = new byte[]{1, 2, 3, 4, 5};
        UploadFileRequest req = new UploadFileRequest(validFile);

        // Upload the file and get the designator
        UploadFileResponse res = interpreterService.uploadFile(req);
        UUID fileDesignator = res.getFileDesignator();

        //check the designator is not null
        assertNotNull(fileDesignator);

        //check that the resultant file can be opened : was saved to the right directory
        String filename = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)
                + fileDesignator
                + configurations.configuration(ConfigurationName.FILE_FORMAT);
        File newlyCreated = new File(filename);

        //check to see that the file contents are the same
        byte[] newContents = Files.readAllBytes(newlyCreated.toPath());
        assertArrayEquals(newContents, validFile);

        newlyCreated = new File(filename);
        //delete the file
        assertTrue(newlyCreated.delete());
    }

    /**
     * Description: tests the uploadFile() function by passing in an empty file
     * precondition - empty byte stream passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Uploading File: input [empty byte stream] expect [empty file exception]")
    public void test_UploadFile_IfEmptyFile_ThenException() {

        // Generate Empty File, put it in request
        UploadFileRequest req = new UploadFileRequest(new byte[]{});

        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class, //for an empty file
                () -> interpreterService.uploadFile(req), //when uploading
                "An empty file should not be processed"); //because

        // Finally, see that the right message was delivered - EMPTY_FILE_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                configurations.configuration(ConfigurationName.EMPTY_FILE_EXCEPTION_TEXT)
        ));
    }

//    /**
//     * Description: tests the uploadFile() function by passing in a file that's too large
//     * precondition - byte stream of the max file size passed in
//     * post condition - correct exception thrown
//     */
//    @Test
//    @DisplayName("Uploading File: input [long byte stream] expect [file too large exception]")
//    public void test_UploadFile_IfHugeFile_ThenException() {
//        // Generate Big File, put it in request
//
//        //max file size in Kb
//        int bigIndex = Integer.parseInt(
//                configurations.configuration(
//                        ConfigurationName.MAX_FILE_UPLOAD_SIZE
//                )
//        );
//
//        //max file size in bytes to be bigger than maximum - 1 larger than specified max
//        bigIndex = 1 + (int) (bigIndex * Math.pow(2, 10));
//        byte[] bigFile = new byte[bigIndex];
//
//        //fill with 0s
//        for (int i = 0; i < bigIndex; i++)
//            bigFile[i] = 0;
//
//        UploadFileRequest req = new UploadFileRequest(bigFile);
//
//        // Check that the error is thrown
//        InvalidUploadException thrown = assertThrows(
//                InvalidUploadException.class, //for a huge file
//                () -> interpreterService.uploadFile(req), //when uploading
//                "A file exceeding the maximum size should not be processed"); //because
//
//        // Finally, see that the right message was delivered - FILE_TOO_LARGE_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_TOO_LARGE_EXCEPTION_TEXT)
//        ));
//    }
//
//
//    /**ProcessFile*/
//    /**ProcessFile
//     * Description: tests the processFile() function by passing in a non-midi file
//     * precondition - fileDesignator for a non-midi file passed in
//     * post condition - correct exception thrown
//     */
//    @Transactional
//    @Rollback(value = true)
//    @Test
//    @DisplayName("Processing File: input [designator for a non-midi file] expect [file mistype exception]")
//    public void test_ProcessFile_IfNonMidiFile_ThenException() throws IOException, InvalidDesignatorException {
//
//        //Create a temporary file to parse
//        UUID fileDesignator = UUID.randomUUID();
//        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);
//
//        //copy temp file from testing data
//        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
//        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_INVALID_TESTING_FILE)).toPath();
//        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
//
//        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
//        ProcessFileResponse res = interpreterService.processFile(req);
//
//        //check that the result is not permitted
//        assertEquals(res.getSuccess(), false);
//        assertEquals(res.getMessage(), configurations.configuration(ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));
//
//        //delete the temporary file
//        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());
//    }
//
//    /**
//     * Description: tests the processFile() function by passing in a midi file designator
//     * that is not in storage
//     * precondition - fileDesignator for a midi file that doesn't exist in storage passed in
//     * post condition - correct exception thrown
//     */
//    @Transactional
//    @Rollback(value = true)
//    @Test
//    @DisplayName("Processing File: input [designator for a file that doesnt exist] expect [file does not exist exception]")
//    public void test_ProcessFile_IfNotInStorage_ThenException() {
//
//        //Create a fake designator
//        UUID fileDesignator = UUID.randomUUID();
//
//        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
//
//        // Check that the error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a file that does not exist
//                () -> interpreterService.processFile(req), //when processing
//                "A file that does not exist cannot be interpreted"); //because
//
//        // Finally, see that the right message was delivered - FILE_TOO_LARGE_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
//        ));
//
//
//    }
//
//    /**
//     * Description: tests the processFile() function by passing in a midi file designator that
//     * exists in storage
//     * precondition - valid fileDesignator in storage passed in
//     * post condition - appropriate success message returned
//     */
//    @Transactional
//    @Rollback(value = true)
//    @Test
//    @DisplayName("Processing File: input [designator for a file that exists] expect [success value and message]")
//    public void test_ProcessFile_IfInStorage_ThenAccurate() throws IOException, InvalidDesignatorException {
//        //Create a temporary file to parse
//        UUID fileDesignator = UUID.randomUUID();
//        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);
//
//        //copy temp file from testing data
//        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
//        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
//        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
//
//        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
//        ProcessFileResponse res = interpreterService.processFile(req);
//
//        //Check that the processing is successful
//        assertEquals(res.getSuccess(), true);
//        assertEquals(res.getMessage(), configurations.configuration(ConfigurationName.SUCCESSFUL_PARSING_TEXT));
//
//        //delete the temporary file
//        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());
//
//    }
//
//    /**
//     * Description: tests the processFile() function by passing in a midi file designator that
//     * already exists in the database
//     * precondition - fileDesignator for file already processed is passed in
//     * post condition - appropriate exception thrown
//     */
//    @Transactional
//    @Rollback(value = true)
//    @Test
//    @DisplayName("Processing File: input [designator for a file that has already been processed] expect [file already processed exception]")
//    public void test_ProcessFile_IfAlreadyInDatabase_ThenException() {
//
//        //Get a designator corresponding to a score in the database - whether or not it actually exists
//        UUID fileDesignator = UUID.fromString(configurations.configuration(
//                ConfigurationName.MIDI_TESTING_DESIGNATOR
//        ));
//
//        //make the request
//        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
//
//        //mock the database with the designator
//        ScoreEntity testEntity = new ScoreEntity();
//        testEntity.setFileDesignator(fileDesignator.toString());
//        databaseManager.save(testEntity);
//
//        // Check that the error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a file that already exists in DB
//                () -> interpreterService.processFile(req), //when processing
//                "A file that already exists in the database cannot be interpreted"); //because
//
//        // Finally, see that the right message was delivered - FILE_ALREADY_EXISTS_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_ALREADY_EXISTS_EXCEPTION_TEXT)
//        ));
//    }
//
//
//    /**InterpretMetre*/
//    /**
//     * Description: tests the interpretMetre() function by passing in a midi file designator that
//     * does not exist in the database
//     * precondition - fileDesignator for file not in Database passed in
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Metre: input [designator for a file not in DB] expect [file does not exist exception]")
//    public void test_InterpretMetre_IfNotInDatabase_ThenException() {
//
//        //Create a fake designator
//        UUID fileDesignator = UUID.randomUUID();
//
//        //make the request
//        InterpretMetreRequest req = new InterpretMetreRequest(fileDesignator);
//
//        // Check that the error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a file that hasn't been interpreted
//                () -> interpreterService.interpretMetre(req), //when interpreting metre
//                "No processing should happen if a file doesn't exist." //because
//        );
//
//        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
//        ));
//    }
//
//    /**
//     * Description: tests the interpretMetre() function by passing in a midi file designator that
//     * does exist in the database
//     * precondition - fileDesignator for midi-file in Database passed in
//     * post condition - Receive beat value and beat number
//     */
//    @Test
//    @DisplayName("Interpret Metre: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
//    public void test_InterpretMetre_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {
//
//        //Get a designator corresponding to a score in the database - whether or not it actually exists
//        UUID fileDesignator = UUID.fromString(configurations.configuration(
//                ConfigurationName.MIDI_TESTING_DESIGNATOR
//        ));
//
//        //mock the database with that designator and timeSignature
//        ScoreEntity testEntity = new ScoreEntity();
//        testEntity.setFileDesignator(fileDesignator.toString());
//        testEntity.setTimeSignature("4/4");
//        databaseManager.save(testEntity);
//
//        //make a request
//        InterpretMetreRequest req = new InterpretMetreRequest(fileDesignator);
//        InterpretMetreResponse res = interpreterService.interpretMetre(req);
//
//        //check that the beat value is a positive power of two
//        int beatValue = res.getMetre().getBeatValue();
//        double c = Math.log(beatValue) / Math.log(2);
//        assertEquals(c, Math.floor(c));
//
//        //check that the number of beats is positive
//        int numBeats = res.getMetre().getNumBeats();
//        assertTrue(numBeats > 0);
//    }
//
//    /**
//     * Description: tests the interpretMetre() function by passing in a midi fileDesignator that does not exist in the database
//     * precondition - no fileDesignator was passed into the function
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Metre: input [empty] expect [empty request exception]")
//    public void test_InterpretMetre_IfEmptyRequest_ThenException() {
//        //Check that the right error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a null request
//                () -> interpreterService.interpretMetre(null), //when interpreting metre
//                "A null request should not be processed."); //because
//
//        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(configurations.configuration(
//                ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
//        )));
//    }
//
//
//    /**InterpretTempo*/
//    /**
//     * Description: tests the interpretTempo() function by passing in a midi fileDesignator that does not exist in the database
//     * precondition - fileDesignator given is not in the database
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Tempo: input [designator for a file not in DB] expect [file does not exist exception]")
//    public void test_InterpretTempo_IfNotInDatabase_ThenException() {
//
//        //Create a fake designator
//        UUID fileDesignator = UUID.randomUUID();
//
//        //make the request
//        InterpretTempoRequest req = new InterpretTempoRequest(fileDesignator);
//
//        // Check that the error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a file that hasn't been interpreted
//                () -> interpreterService.interpretTempo(req), //when interpreting tempo
//                "No processing should happen if a file doesn't exist." //because
//        );
//
//        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
//        ));
//
//    }
//
//    /**
//     * Description: tests the interpretTempo() function by passing in a midi fileDesignator that exists in the database
//     * precondition - fileDesignator for midi-file in Database passed in
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Metre: input [designator for a file in DB] expect [a positive integer]")
//    public void test_InterpretTempo_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {
//
//        //Get a designator corresponding to a score in the database - whether or not it actually exists
//        UUID fileDesignator = UUID.fromString(configurations.configuration(
//                ConfigurationName.MIDI_TESTING_DESIGNATOR
//        ));
//
//        //mock the database with the designator and tempoIndication
//        ScoreEntity testEntity = new ScoreEntity();
//        testEntity.setFileDesignator(fileDesignator.toString());
//        testEntity.setTempoIndication(50);
//        databaseManager.save(testEntity);
//
//        //make a request
//        InterpretTempoRequest req = new InterpretTempoRequest(fileDesignator);
//        InterpretTempoResponse res = interpreterService.interpretTempo(req);
//
//        //see that the tempo is a positive integer
//        TempoIndication t = res.getTempo();
//        assertTrue(t.getTempo() > 0);
//    }
//
//    /**
//     * Description: tests the interpretTempo() function by passing in no parameters
//     * precondition - no parameters passed in
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Tempo: input [empty] expect [empty request exception]")
//    public void test_InterpretTempo_IfEmptyRequest_ThenException() {
//        //Check that the right error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a null request
//                () -> interpreterService.interpretTempo(null), //when interpreting Tempo
//                "A null request should not be processed."); //because
//
//        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(configurations.configuration(
//                ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
//        )));
//    }
//
//
//    /**InterpretKeySignature*/
//    /**
//     * Description: tests the interpretSignature() function by passing in a fileDesignator that is not in the database
//     * precondition - fileDesignator is not in the database
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Key Signature: input [designator for a file not in DB] expect [file does not exist exception]")
//    public void test_InterpretKeySignature_IfNotInDatabase_ThenException() {
//
//        //Create a fake designator
//        UUID fileDesignator = UUID.randomUUID();
//
//        //make the request
//        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(fileDesignator);
//
//        // Check that the error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a file that hasn't been interpreted
//                () -> interpreterService.interpretKeySignature(req), //when interpreting KeySignature
//                "No processing should happen if a file doesn't exist." //because
//        );
//
//        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
//        ));
//    }
//
//    /**
//     * Description: tests the interpretSignature() function by passing in a fileDesignator that is in the database
//     * precondition - fileDesignator is in the database
//     * post condition - Receive key signature and signature name
//     */
//    @Test
//    @DisplayName("Interpret Metre: input [designator for a file in DB] expect [a valid key signature string]")
//    public void test_InterpretKeySignature_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {
//
//        //Get a designator corresponding to a score in the database - whether or not it actually exists
//        UUID fileDesignator = UUID.fromString(configurations.configuration(
//                ConfigurationName.MIDI_TESTING_DESIGNATOR
//        ));
//
//        //mock the database with the designator and keySignature
//        ScoreEntity testEntity = new ScoreEntity();
//        testEntity.setFileDesignator(fileDesignator.toString());
//        testEntity.setKeySignature("Ebmaj");
//        databaseManager.save(testEntity);
//
//        //make a request
//        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(fileDesignator);
//        InterpretKeySignatureResponse res = interpreterService.interpretKeySignature(req);
//
//        //Check that the key is valid
//        String[] keyArray = {"Cbmaj", "Gbmaj", "Dbmaj", "Abmaj", "Ebmaj", "Bbmaj", "Fmaj", "Cmaj", "Gmaj", "Dmaj", "Amaj", "Emaj", "Bmaj", "F#maj", "C#maj", "Abmin", "Ebmin", "Bbmin", "Fmin", "Cmin", "Gmin", "Dmin", "Amin", "Emin", "Bmin", "F#min", "C#min", "G#min", "D#min", "A#min"};
//        boolean b = Arrays.asList(keyArray).contains(res.getKeySignature().getSignatureName());
//        assertTrue(b);
//    }
//
//    /**
//     * Description: tests the interpretSignature() function by passing in no parameters
//     * precondition - no parameters passed in
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Interpret Key Signature: input [empty] expect [empty request exception]")
//    public void test_InterpretKeySignature_IfEmptyRequest_ThenException() {
//        //Check that the right error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for a null request
//                () -> interpreterService.interpretKeySignature(null), //when interpreting KeySignature
//                "A null request should not be processed."); //because
//
//        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
//        assertTrue(thrown.getMessage().contains(configurations.configuration(
//                ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
//        )));
//    }
//
//
//    /**ParseJSON*/
//    /**
//     * Description: tests the parseJSON() function by passing in a midi file designator that is not in storage
//     * precondition - fileDesignator for a midi file that doesn't exist in storage passed in
//     * post condition - appropriate exception thrown
//     */
//    @Test
//    @DisplayName("Parsing JSON: input [designator for a file that doesnt exist] expect [file does not exist exception]")
//    public void test_ParseJSON_IfNotInStorage_ThenException() {
//
//        // Generate a new UUID - is unique and so is different from all existing in storage
//        UUID fileDesignator = UUID.randomUUID();
//
//        // Make a request with the new designator
//        ParseJSONRequest req = new ParseJSONRequest(fileDesignator);
//
//        // Check that the error is thrown
//        InvalidDesignatorException thrown = assertThrows(
//                InvalidDesignatorException.class, //for invalid designator - no file
//                () -> interpreterService.parseJSON(req), //when parsing
//                "No file with this designator should exists in storage" //because
//        );
//
//        // Finally, see that the right message was delivered - File does not exist
//        assertTrue(thrown.getMessage().contains(
//                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
//        ));
//
//    }

    /**
     * Description: tests the parseJSON() function by passing in a midi file designator that is in storage
     * precondition - fileDesignator for a midi file that exists in storage passed in
     * post condition - appropriate success message received
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Parsing JSON: input [designator for a file that exists] expect [a score with several details met]")
    public void test_ParseJSON_IfInStorage_ThenAccurate() throws Exception {

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        //interpret the work and get the score
        ParseJSONRequest req = new ParseJSONRequest(fileDesignator);
        ParseJSONResponse res = interpreterService.parseJSON(req);
        Score score = res.getParsedScore();

        //delete the temporary file
        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());

        //has a track
        assertTrue(score.channelList.size()>0);

        //within max amount of tracks
        assertTrue(score.channelList.size()<=16);

        //1.2 There is a valid key signature for every tick
        String[] keyArray = {"Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#"};
        for(KeySignature k : score.KeySignatureMap){
            //check the key is a key
            boolean b = Arrays.asList(keyArray).contains(k.commonName);
            assertTrue(b);
            //check the tick is valid
            assertTrue(k.tick >= 0);
        }


        //1.3 The tempo is a positive integer
        for(TempoIndication t : score.TempoIndicationMap){
            //valid tempo is positive
            assertTrue(t.tempoIndication > 0);
            //check the tick is valid
            assertTrue(t.tick >= 0);
        }

        for(TimeSignature t : score.TimeSignatureMap){
            //The beat value is an integer power of two
            int beatValue = t.time.beatValue;
            double c = Math.log(beatValue) / Math.log(2);
            assertEquals(c, Math.floor(c));

            //The beat number is positive
            int numBeats = t.time.numBeats;
            assertTrue(numBeats > 0);

            //check the tick is valid
            assertTrue(t.tick >= 0);
        }

        for(Channel c : score.channelList){
            int channelNum = c.channelNumber;
            //check the channel is valid
            assertTrue(channelNum>=0);
            assertTrue(channelNum<=15);

            //check there is an insrument name
            assertNotEquals(c.instrument,"");

            //check the ticks per beat is valid
            assertTrue(c.ticksPerBeat > 0);
        }
        

    }



//
//    //====================================================================================================================//
//    //                                  WHITE BOX TESTING BELOW                                                           //
//    //====================================================================================================================//
//
//    /**UploadFile*/
//    /**
//     * Description: tests the uploadFile() function by passing in a valid file and saving
//     * to the right directory
//     * precondition - valid byte stream passed in
//     * post condition - valid UUID from the right directory with the sames contents
//     */
//    @Test
//    @DisplayName("Uploading File: input [valid byte stream] expect [valid UUID corresponding to file with same contents]")
//    public void testWhiteBox_UploadFile_IfValidFile_ThenAccurateInfo() throws InvalidUploadException, IOException {
//
//        // Generate Valid File, put it in request
//        byte[] validFile = new byte[]{1, 2, 3, 4, 5};
//        UploadFileRequest req = new UploadFileRequest(validFile);
//
//        // Upload the file and get the designator
//        UploadFileResponse res = interpreterService.uploadFile(req);
//        UUID fileDesignator = res.getFileDesignator();
//
//        //check the designator is not null
//        assertNotNull(fileDesignator);
//
//        ScoreEntity testEntity = new ScoreEntity();
//        testEntity.setFileDesignator(fileDesignator.toString());
//        testEntity.setKeySignature("Cbmaj");
//        testEntity.setTempoIndication(70);
//        testEntity.setTimeSignature("4/4");
//        databaseManager.save(testEntity);
//
//        //check that the resultant file can be opened : was saved to the right directory
//        String filename = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)
//                + fileDesignator
//                + configurations.configuration(ConfigurationName.FILE_FORMAT);
//        File newlyCreated = new File(filename);
//
//        //check to see that the file contents are the same
//        byte[] newContents = Files.readAllBytes(newlyCreated.toPath());
//        assertArrayEquals(newContents, validFile);
//
//        newlyCreated = new File(filename);
//        //delete the file
//        assertTrue(newlyCreated.delete());
//    }
//
//    /**
//     * Description: tests the processFile() function by passing in a midi file designator that
//     * exists in storage
//     * precondition - valid fileDesignator in storage passed in
//     * post condition - appropriate success message returned
//     */
//    @Transactional
//    @Rollback(value = true)
//    @Test
//    @DisplayName("Processing File: input [designator for a file that exists] expect [success value and message]")
//    public void testWhiteBox_ProcessFile_IfInStorage_ThenAccurate() throws IOException, InvalidDesignatorException {
//        //Create a temporary file to parse
//        UUID fileDesignator = UUID.randomUUID();
//        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);
//
//        //copy temp file from testing data
//        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
//        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
//        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
//
//        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
//        ProcessFileResponse res = interpreterService.processFile(req);
//
//        //Check that the processing is successful
//        assertEquals(res.getSuccess(), true);
//        assertEquals(res.getMessage(), configurations.configuration(ConfigurationName.SUCCESSFUL_PARSING_TEXT));
//
//        //delete the temporary file
//        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());
//
//    }
//
//    /**
//     * Description: tests the interpretMetre() function by passing in a midi file designator that
//     * does exist in the database
//     * precondition - fileDesignator for midi-file in Database passed in
//     * post condition - Receive beat value and beat number
//     */
//    @Test
//    @DisplayName("Interpret Metre: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
//    public void testWhiteBox_InterpretMetre_IfInDatabase_ThenAccurate() throws InvalidDesignatorException {
//
//        //Get a designator corresponding to a score in the database - whether or not it actually exists
//        UUID fileDesignator = UUID.fromString(configurations.configuration(
//                ConfigurationName.MIDI_TESTING_DESIGNATOR
//        ));
//
//        //mock the database with that designator and timeSignature
//        ScoreEntity testEntity = new ScoreEntity();
//        testEntity.setFileDesignator(fileDesignator.toString());
//        testEntity.setTimeSignature("4/4");
//        databaseManager.save(testEntity);
//
//        //make a request
//        InterpretMetreRequest req = new InterpretMetreRequest(fileDesignator);
//        InterpretMetreResponse res = interpreterService.interpretMetre(req);
//
//        //check that the beat value is a positive power of two
//        int beatValue = res.getMetre().getBeatValue();
//        double c = Math.log(beatValue) / Math.log(2);
//        assertEquals(c, Math.floor(c));
//
//        //check that the number of beats is positive
//        int numBeats = res.getMetre().getNumBeats();
//        assertTrue(numBeats > 0);
//    }
//


    /**
     * Description: tests the parseJSON() function by passing in a midi file designator that is in storage
     * precondition - fileDesignator for a midi file that exists in storage passed in
     * post condition - appropriate success message received
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Parsing JSON: input [designator for a file that exists] expect [a score with several details met]")
    public void testWhiteBox_ParseJSON_IfInStorage_ThenAccurate() throws Exception {

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        //interpret the work and get the score
        ParseJSONRequest req = new ParseJSONRequest(fileDesignator);
        ParseJSONResponse res = interpreterService.parseJSON(req);
        Score score = res.getParsedScore();

        //delete the temporary file
        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());

        //1.1 ensure track list has no more than 16 tracks
        //has a track
        assertTrue(score.channelList.size()>0);

        //within max amount of tracks
        assertTrue(score.channelList.size()<=16);

        //1.2 There is a valid key signature for every tick
        String[] keyArray = {"Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#"};
        for(KeySignature k : score.KeySignatureMap){
            //check the key is a key
            boolean b = Arrays.asList(keyArray).contains(k.commonName);
            assertTrue(b);
            //check the tick is valid
            assertTrue(k.tick >= 0);
        }

        //1.3 The tempo is a positive integer
        for(TempoIndication t : score.TempoIndicationMap){
            //valid tempo is positive
            assertTrue(t.tempoIndication > 0);
            //check the tick is valid
            assertTrue(t.tick >= 0);
        }

        //1.4 The beat value is an integer power of two
        for(TimeSignature t : score.TimeSignatureMap){
            //The beat value is an integer power of two
            int beatValue = t.time.beatValue;
            double c = Math.log(beatValue) / Math.log(2);
            assertEquals(c, Math.floor(c));

            //The beat number is positive
            int numBeats = t.time.numBeats;
            assertTrue(numBeats > 0);

            //check the tick is valid
            assertTrue(t.tick >= 0);
        }

        //1.6 for all tracks
        for(Channel c : score.channelList){
            int channelNum = c.channelNumber;
            //check the channel is valid
            assertTrue(channelNum>=0);
            assertTrue(channelNum<=15);

            //check there is an instrument name
            assertNotEquals(c.instrument,"");

            //check the ticks per beat is valid
            assertTrue(c.ticksPerBeat > 0);
        }
      }

   }
//
//
//}
