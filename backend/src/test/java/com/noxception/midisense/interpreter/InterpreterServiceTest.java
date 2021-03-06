package com.noxception.midisense.interpreter;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.dataclass.MockRepository;
import com.noxception.midisense.dataclass.MockRequestBroker;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.*;
import com.noxception.midisense.interpreter.rrobjects.*;
import com.noxception.midisense.repository.DatabaseManager;
import com.noxception.midisense.repository.ScoreEntity;
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


class InterpreterServiceTest {

    //services and configs
    private InterpreterService interpreterService;
    private StandardConfig configurations;
    private DatabaseManager databaseManager;

    private final String[] keyArray = {"Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#"};

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

    /**
     * Description: tests the uploadFile() function by passing in a file that's too large
     * precondition - byte stream of the max file size passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Uploading File: input [long byte stream] expect [file too large exception]")
    public void test_UploadFile_IfHugeFile_ThenException() {
        // Generate Big File, put it in request

        //max file size in Kb
        int bigIndex = Integer.parseInt(
                configurations.configuration(
                        ConfigurationName.MAX_FILE_UPLOAD_SIZE
                )
        );

        //max file size in bytes to be bigger than maximum - 1 larger than specified max
        bigIndex = 1 + (int) (bigIndex * Math.pow(2, 10));
        byte[] bigFile = new byte[bigIndex];

        //fill with 0s
        for (int i = 0; i < bigIndex; i++)
            bigFile[i] = 0;

        UploadFileRequest req = new UploadFileRequest(bigFile);

        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class, //for a huge file
                () -> interpreterService.uploadFile(req), //when uploading
                "A file exceeding the maximum size should not be processed"); //because

        // Finally, see that the right message was delivered - FILE_TOO_LARGE_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                configurations.configuration(ConfigurationName.FILE_TOO_LARGE_EXCEPTION_TEXT)
        ));
    }


    /**ProcessFile*/
    /**ProcessFile
     * Description: tests the processFile() function by passing in a non-midi file
     * precondition - fileDesignator for a non-midi file passed in
     * post condition - correct exception thrown
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Processing File: input [designator for a non-midi file] expect [file mistype exception]")
    public void test_ProcessFile_IfNonMidiFile_ThenException() throws IOException, InvalidDesignatorException {

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_INVALID_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        //set the message broker to reject the interpretation
        MockRequestBroker requestBroker = new MockRequestBroker(this.configurations);
        requestBroker.isRejecting();
        ((InterpreterServiceImpl) interpreterService).addDefaultBroker(requestBroker);

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
        ProcessFileResponse res = interpreterService.processFile(req);

        //check that the result is not permitted
        assertEquals(res.getSuccess(), false);
        assertEquals(res.getMessage(), configurations.configuration(ConfigurationName.INVALID_MIDI_EXCEPTION_TEXT));

        //delete the temporary file
        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());
    }

    /**
     * Description: tests the processFile() function by passing in a midi file designator
     * that is not in storage
     * precondition - fileDesignator for a midi file that doesn't exist in storage passed in
     * post condition - correct exception thrown
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Processing File: input [designator for a file that doesnt exist] expect [file does not exist exception]")
    public void test_ProcessFile_IfNotInStorage_ThenException() {

        //Create a fake designator
        UUID fileDesignator = UUID.randomUUID();

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that does not exist
                () -> interpreterService.processFile(req), //when processing
                "A file that does not exist cannot be interpreted"); //because

        // Finally, see that the right message was delivered - FILE_TOO_LARGE_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));


    }

    /**
     * Description: tests the processFile() function by passing in a midi file designator that
     * exists in storage
     * precondition - valid fileDesignator in storage passed in
     * post condition - appropriate success message returned
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Processing File: input [designator for a file that exists] expect [success value and message]")
    public void test_ProcessFile_IfInStorage_ThenAccurate() throws IOException, InvalidDesignatorException {
        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
        ProcessFileResponse res = interpreterService.processFile(req);

        //Check that the processing is successful
        assertEquals(res.getSuccess(), true);
        assertEquals(res.getMessage(), configurations.configuration(ConfigurationName.SUCCESSFUL_PARSING_TEXT));

    }

    /**
     * Description: tests the processFile() function by passing in a midi file designator that
     * already exists in the database
     * precondition - fileDesignator for file already processed is passed in
     * post condition - appropriate exception thrown
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Processing File: input [designator for a file that has already been processed] expect [file already processed exception]")
    public void test_ProcessFile_IfAlreadyInDatabase_ThenException() {

        //Get a designator corresponding to a score in the database - whether or not it actually exists
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //make the request
        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);

        //mock the database with the designator
        ScoreEntity testEntity = new ScoreEntity();
        testEntity.setFileDesignator(fileDesignator.toString());
        databaseManager.save(testEntity);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for a file that already exists in DB
                () -> interpreterService.processFile(req), //when processing
                "A file that already exists in the database cannot be interpreted"); //because

        // Finally, see that the right message was delivered - FILE_ALREADY_EXISTS_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(
                configurations.configuration(ConfigurationName.FILE_ALREADY_EXISTS_EXCEPTION_TEXT)
        ));
    }




    /**ParseJSON*/
    /**
     * Description: tests the parseJSON() function by passing in a midi file designator that is not in storage
     * precondition - fileDesignator for a midi file that doesn't exist in storage passed in
     * post condition - appropriate exception thrown
     */
    @Test
    @DisplayName("Parsing JSON: input [designator for a file that doesnt exist] expect [file does not exist exception]")
    public void test_ParseJSON_IfNotInStorage_ThenException() {

        // Generate a new UUID - is unique and so is different from all existing in storage
        UUID fileDesignator = UUID.randomUUID();

        // Make a request with the new designator
        ParseJSONRequest req = new ParseJSONRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class, //for invalid designator - no file
                () -> interpreterService.parseJSON(req), //when parsing
                "No file with this designator should exists in storage" //because
        );

        // Finally, see that the right message was delivered - File does not exist
        assertTrue(thrown.getMessage().contains(
                configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)
        ));

    }

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
    /**UploadFile*/
    /**
     * Description: tests the uploadFile() function by passing in a valid file and saving
     * to the right directory
     * precondition - valid byte stream passed in
     * post condition - valid UUID from the right directory with the sames contents
     */
    @Test
    @DisplayName("Uploading File: input [valid byte stream] expect [valid UUID corresponding to file with same contents]")
    public void testWhiteBox_UploadFile_IfValidFile_ThenAccurateInfo() throws InvalidUploadException, IOException {

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
     * Description: tests the processFile() function by passing in a midi file designator that
     * exists in storage
     * precondition - valid fileDesignator in storage passed in
     * post condition - appropriate success message returned
     */
    @Transactional
    @Rollback(value = true)
    @Test
    @DisplayName("Processing File: input [designator for a file that exists] expect [success value and message]")
    public void testWhiteBox_ProcessFile_IfInStorage_ThenAccurate() throws IOException, InvalidDesignatorException {
        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        ProcessFileRequest req = new ProcessFileRequest(fileDesignator);
        ProcessFileResponse res = interpreterService.processFile(req);

        //Check that the processing is successful
        assertEquals(res.getSuccess(), true);
        assertEquals(res.getMessage(), configurations.configuration(ConfigurationName.SUCCESSFUL_PARSING_TEXT));

    }



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
