package com.noxception.midisense.display;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.dataclass.MockRepository;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.parser.KeySignature;
import com.noxception.midisense.interpreter.parser.TempoIndication;
import com.noxception.midisense.interpreter.parser.TimeSignature;
import com.noxception.midisense.interpreter.parser.Track;
import com.noxception.midisense.repository.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DisplayServiceTest extends MIDISenseUnitTest {


    private DisplayServiceImpl displayService;
    private StandardConfig configurations;
    private DatabaseManager databaseManager;

    @BeforeEach
    public void mountModule() {
        databaseManager = new MockRepository();
        configurations = new MockConfigurationSettings();
        displayService = new DisplayServiceImpl(databaseManager,configurations);
    }


    //====================================================================================================================//
    //                                  BLACK BOX TESTING BELOW                                                           //
    //====================================================================================================================//


    /**GetPieceMetaData*/

    /**Description: tests the getPieceMetadata() function by passing in a valid UUID and
     * the entry is in the database
     * precondition - valid UUID in database passed in
     * post condition - returned data is accurate
     */
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
    public void test_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(fileDesignator);

        //Get response
        GetPieceMetadataResponse res = displayService.getPieceMetadata(req);


        //Check that the key signature is valid
        String[] keyArray = {"Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#"};
        for(KeySignature k : res.getKeySignature()){
            assertTrue(Arrays.asList(keyArray).contains(k.commonName));
        }


        //Check the tempo is an integer greater than 0
        for(TempoIndication t : res.getTempoIndication()){
            assertTrue(t.tempoIndication>0);
        }


        //Check the beat value for time signature is a power of two (Greater than one)
        for(TimeSignature ts : res.getTimeSignature()){
            int beatValue = ts.time.beatValue;
            double c = Math.log(beatValue)/Math.log(2);
            assertEquals(c,Math.floor(c));
        }


        //Check the beat number for time signature is a positive integer
        for(TimeSignature ts : res.getTimeSignature()){
            int numBeats = ts.time.numBeats;
            assertTrue(numBeats>0);
        }


    }

    /**Description: tests the getPieceMetadata() function by passing in a valid UUID
     * and the entry is not in the database
     * precondition - valid UUID not in database passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file not in DB] expect [file does not exist exception]")
    public void test_GetPieceMetadata_IfNotInDatabase_ThenException() {

        //get a fake designator
        UUID fileDesignator = UUID.randomUUID();

        //make request
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getPieceMetadata(req),//when function called
                "No processing should happen if a file doesn't exist in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        )));
    }

    /**Description: tests the getTPieceMetadata() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Piece Metadata: input [empty] expect [empty request exception]")
    public void test_GetPieceMetadata_IfEmptyRequest_ThenException() {

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for an empty request
                ()->displayService.getPieceMetadata(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
        )));
    }




    /**GetTrackInfo*/

    /**Description: tests the getTrackInfo() function by passing in a valid UUID and
     * the entry is in the database
     * precondition - valid UUID in database passed in
     * post condition - returned data is accurate
     */
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [A map consisting of at least 1 track]")
    public void test_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetTrackInfoRequest req = new GetTrackInfoRequest(fileDesignator);

        //Get response
        GetTrackInfoResponse res = displayService.getTrackInfo(req);

        //Check we receive an array back with at least one entry in it
        assertFalse(res.getTrackMap().isEmpty());


    }

    /**Description: tests the getTrackInfo() function by passing in a valid UUID
     * and the entry is not in the database
     * precondition - valid UUID not in database passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Info: input [Designator for file not in DB] expect [file does not exist exception]")
    public void test_GetTrackInfo_IfNotInDatabase_ThenException() {

        //get a fake designator
        UUID fileDesignator = UUID.randomUUID();

        //Make request
        GetTrackInfoRequest req = new GetTrackInfoRequest(fileDesignator);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackInfo(req),//when function called
                "No processing should happen if file not in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        )));
    }

    /**Description: tests the getTrackInfo() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Info: input [empty] expect [empty request exception]")
    public void test_GetTrackInfo_IfEmptyRequest_ThenException() {

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for an empty request
                ()->displayService.getTrackInfo(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
        )));
    }





    /**GetTrackMetadata*/


    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and valid Track
     * and the entry is in the database
     * precondition - valid UUID, valid Track passed in
     * post condition - returned data is accurate
     */
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
    public void test_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException, InvalidTrackException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(configurations.configuration(
                ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));


        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator, (byte) validTrackIndex);

        //Get response
        GetTrackMetadataResponse res = displayService.getTrackMetadata(req);


        assertTrue(res.getTrackString().contains("\"notes\":[{"));
    }


    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Invalid Track (Too High)
     * and the entry is in the database
     * precondition - valid UUID, invalid Track (16 - too high for a track index) passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [invalid track index exception]")
    public void test_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenException() {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get an invalid track index - too high
        int invalidTrackIndex = 16;


        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,(byte) invalidTrackIndex);

        //Check the error is thrown
        InvalidTrackException thrown = assertThrows(
                InvalidTrackException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the track index is invalid.");//because

        // Finally, see that the right message was delivered - INVALID_TRACK_INDEX_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT
        )));

    }


    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Invalid Track (Too Low)
     * and the entry is in the database
     * precondition - valid UUID, invalid Track (-1 - format error) passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [invalid track index exception]")
    public void test_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenException() {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get an invalid track index - too high
        int invalidTrackIndex = -1;

        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,(byte) invalidTrackIndex);

        //Check the error is thrown
        InvalidTrackException thrown = assertThrows(
                InvalidTrackException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the track index is invalid.");//because

        // Finally, see that the right message was delivered - INVALID_TRACK_INDEX_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT
        )));

    }


    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Valid Track
     * but the entry is not in the database
     * precondition - valid UUID, valid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and valid track index] expect [invalid designator exception]")
    public void test_GetTrackMetadata_IfNotInDatabaseAndValidTrack_ThenException() {

        //Get a fake designator
        UUID fileDesignator = UUID.randomUUID();

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(configurations.configuration(
                ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));


        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,TestingDictionary.display_all_valid_track_index);

        //Check the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the entry does not exist in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));

    }


    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Invalid Track
     * but the entry is not in the database
     * precondition - valid UUID, invalid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and invalid track index] expect [invalid designator and invalid track index exceptions]")
    public void test_GetTrackMetadata_IfNotInDatabaseAndInvalidTrack_ThenException() {
        //Generate random UUID
        UUID fileDesignator = UUID.randomUUID();

        //The index isn't important as the designator should be triggered first
        int index = 1;

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,(byte) index);

        //Check the error is thrown
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the entry does not exist in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));

    }


    /**Description: tests the getTrackMetadata() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    @DisplayName("Get Track Metadata: input [empty] expect [empty request exception]")
    public void test_GetTrackMetadata_IfEmptyRequest_ThenException() {
        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for an empty request
                ()->displayService.getTrackMetadata(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(configurations.configuration(
                ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT
        )));

    }










    //====================================================================================================================//
    //                                  WHITE BOX TESTING BELOW                                                                //
    //====================================================================================================================//




    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
    public void testWhiteBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(fileDesignator);

        //Get response
        GetPieceMetadataResponse res = displayService.getPieceMetadata(req);


        //Check that the key signature is valid
        String[] keyArray = {"Cb"};
        for(KeySignature k : res.getKeySignature()){
            assertTrue(Arrays.asList(keyArray).contains(k.commonName));
        }


        //Check the tempo is an integer greater than 0
        for(TempoIndication t : res.getTempoIndication()){
            assertEquals(70, t.tempoIndication);
        }


        //Check the beat value for time signature is a power of two (Greater than one)
        for(TimeSignature ts : res.getTimeSignature()){
            int beatValue = ts.time.beatValue;
            assertEquals(4,beatValue);
        }


        //Check the beat number for time signature is a positive integer
        for(TimeSignature ts : res.getTimeSignature()){
            int numBeats = ts.time.numBeats;
            assertEquals(4, numBeats);
        }
    }

    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [A map consisting of at least 1 track]")
    public void testWhiteBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetTrackInfoRequest req = new GetTrackInfoRequest(fileDesignator);

        //Get response
        GetTrackInfoResponse res = displayService.getTrackInfo(req);


        //Check we receive an array back with at least one entry in it
        assertFalse(res.getTrack((byte) 0).isEmpty());

        //check the entry to be what we put in
        assertEquals("Electric Bass (pick)", res.getTrack((byte) 0));

    }

    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
    public void testWhiteBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(configurations.configuration(
                ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));

        //generate a score for the db
        ((MockRepository) this.databaseManager).generateMockScore(fileDesignator);

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator, (byte) validTrackIndex);

        //Get response
        GetTrackMetadataResponse res = displayService.getTrackMetadata(req);

        //Check that there is a substring with correct values
        assertTrue(res.getTrackString().contains("{\"channel\":1,\"instrument\":\"Electric Bass (pick)\",\"ticks_per_beat\":92,\"track\":[{\"tick\":0,\"notes\":[{\"value\":69,\"on_velocity\":100,\"off_velocity\":0,\"duration\":600}]}]}"));
    }



}

