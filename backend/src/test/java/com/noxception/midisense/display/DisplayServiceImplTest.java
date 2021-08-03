package com.noxception.midisense.display;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.parser.Track;
import com.noxception.midisense.interpreter.rrobjects.InterpretMetreRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class DisplayServiceImplTest extends MIDISenseUnitTest {

    @Autowired
    private DisplayServiceImpl displayService;

    @BeforeEach
    public void setUp() {
        LogType[] monitorList = {LogType.DEBUG};
        this.monitor(monitorList);
    }
    /** ************************************************************************************ */

    /**GetPieceMetaData*/
    /**Description: tests the getPieceMetadata() function by passing in a valid UUID and
     * the entry is in the database
     * precondition - valid UUID in database passed in
     * post condition - returned data is accurate
     */
    @Test
    public void test_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {
        //Make request
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator));

        //Get response
        GetPieceMetadataResponse res = displayService.getPieceMetadata(req);

        //Check that the key signature is valid
        String[] keyArray = {"Cbmaj", "Gbmaj", "Dbmaj", "Abmaj", "Ebmaj", "Bbmaj", "Fmaj", "Cmaj", "Gmaj", "Dmaj", "Amaj", "Emaj", "Bmaj", "F#maj", "C#maj", "Abmin", "Ebmin", "Bbmin", "Fmin", "Cmin", "Gmin", "Dmin", "Amin", "Emin", "Bmin", "F#min", "C#min", "G#min", "D#min", "A#min"};
        boolean b = Arrays.asList(keyArray).contains(res.getKeySignature().getSignatureName());
        assertTrue(b);

        //Check the tempo is an integer greater than 0
        assertTrue(res.getTempoIndication().getTempo()>0);


        // Check the beat value for time signature is a power of two (Greater than one)
        int beatValue = res.getTimeSignature().getBeatValue();
        double c = Math.log(beatValue)/Math.log(2);
        assertEquals(c,Math.floor(c));


        //Check the beat number for time signature is a positive integer
        int numBeats = res.getTimeSignature().getNumBeats();
        assertTrue(numBeats>0);

    }

    /**Description: tests the getPieceMetadata() function by passing in a valid UUID
     * and the entry is not in the database
     * precondition - valid UUID not in database passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetPieceMetadata_IfNotInDatabase_ThenException() {
        //make request
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator));

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getPieceMetadata(req),//when function called
                "No processing should happen if a file doesn't exist in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    /**Description: tests the getTPieceMetadata() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetPieceMetadata_IfEmptyRequest_ThenException() {
        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class,//for an empty request
                ()->displayService.getPieceMetadata(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }


    /**GetTrackInfo*/
    /**Description: tests the getTrackInfo() function by passing in a valid UUID and
     * the entry is in the database
     * precondition - valid UUID in database passed in
     * post condition - returned data is accurate
     */
    @Test
    public void test_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {
        //Make request
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator));

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
    public void test_GetTrackInfo_IfNotInDatabase_ThenException() {
        //Make request
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator));

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackInfo(req),//when function called
                "No processing should happen if file not in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    /**Description: tests the getTrackInfo() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackInfo_IfEmptyRequest_ThenException() {
        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class,//for an empty request
                ()->displayService.getTrackInfo(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }


    /**GetTrackMetadata*/
    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and valid Track
     * and the entry is in the database
     * precondition - valid UUID, valid Track passed in
     * post condition - returned data is accurate
     */
    @Test
    public void test_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException {

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator, (byte) 0);

        //Get response
        GetTrackMetadataResponse res = displayService.getTrackMetadata(req);

        //Check that there is a substring for an inner array with at least one item
        Pattern validResponse = Pattern.compile("\\{\\\"notes\\\": \\[(\\{.+\\})*\\]\\}",Pattern.MULTILINE);
        Matcher matcher = validResponse.matcher(res.getTrackString());

        //see that the substring is present
        assertTrue(matcher.find());
    }

    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Invalid Track
     * and the entry is in the database
     * precondition - valid UUID, invalid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackMetadata_IfPresentInDatabaseWithInValidTrackAndInvalidID_ThenAccurateInfo() {
        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator),TestingDictionary.display_all_invalid_track_index);

        //Check the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the track index is invalid.");//because

        // Finally, see that the right message was delivered - INVALID_TRACK_INDEX_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT)));

    }

    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Valid Track
     * but the entry is not in the database
     * precondition - valid UUID, valid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackMetadata_IfNotInDatabaseAndValidTrack_ThenException() {
        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator),TestingDictionary.display_all_valid_track_index);

        //Check the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the entry does not exist in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));

    }

    /**Description: tests the getTrackMetadata() function by passing in a valid UUID and Invalid Track
     * but the entry is not in the database
     * precondition - valid UUID, invalid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackMetadata_IfNotInDatabaseAndInvalidTrack_ThenException() {
        //Generate random UUID
        UUID fileDesignator = UUID.randomUUID();

        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,TestingDictionary.display_all_invalid_track_index);

        //Check the error is thrown
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackMetadata(req),//when function called
                "No processing should happen if the entry does not exist in the database.");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));

    }

    /**Description: tests the getTrackMetadata() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackMetadata_IfEmptyRequest_ThenException() {
        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class,//for an empty request
                ()->displayService.getTrackMetadata(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));

    }


    /**GetTrackOverview*/
    /**Description: tests the getTrackOverview() function by passing in a valid UUID and valid Track
     * and the entry is in the database
     * precondition - valid UUID, valid Track passed in
     * post condition - returned data is accurate
     */
    @Test
    public void test_GetTrackOverview_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException {
        //Make request
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);

        //Get a response
        GetTrackOverviewResponse res = displayService.getTrackOverview(req);

        //Check the array has at least one item
        assertFalse(res.getPitchArray().isEmpty());
    }

    /**Description: tests the getTrackOverview() function by passing in a valid UUID and Invalid Track
     * and the entry is in the database
     * precondition - valid UUID, invalid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackOverview_IfPresentInDatabaseWithInValidTrackAndInvalidID_ThenException() {
        //Make request
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator),TestingDictionary.display_all_invalid_track_index);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackOverview(req),//when function called
                "No processing should happen if the track index is invalid.");//because

        // Finally, see that the right message was delivered - INVALID_TRACK_INDEX_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT)));

    }

    /**Description: tests the getTrackOverview() function by passing in a valid UUID and Valid Track
     * but the entry is not in the database
     * precondition - valid UUID, valid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackOverview_IfNotInDatabaseAndValidTrack_ThenException() {
        //Make request
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator),TestingDictionary.display_all_valid_track_index);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackOverview(req),//when function called
                "No processing should happen if the entry does not exist in the database");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    /**Description: tests the getTrackOverview() function by passing in a valid UUID and Invalid Track
     * but the entry is not in the database
     * precondition - valid UUID, invalid Track passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackOverview_IfNotInDatabaseAndInvalidTrack_ThenException() {
        //Generate random UUID
        UUID fileDesignator = UUID.randomUUID();

        //Make request
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(fileDesignator,TestingDictionary.display_all_invalid_track_index);

        // Check that the error is thrown
        InvalidDesignatorException thrown = assertThrows(
                InvalidDesignatorException.class,//for a request
                ()->displayService.getTrackOverview(req),//when function called
                "No processing should happen if the entry does not exist in the database");//because

        // Finally, see that the right message was delivered - FILE_DOES_NOT_EXIST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));

    }

    /**Description: tests the getTrackOverview() function by passing in an empty request
     * precondition - empty request passed in
     * post condition - correct exception thrown
     */
    @Test
    public void test_GetTrackOverview_IfEmptyRequest_ThenException() {
        // Check that the error is thrown
        InvalidUploadException thrown = assertThrows(
                InvalidUploadException.class,//for an empty request
                ()->displayService.getTrackOverview(null),//when function called
                "A null request should not be processed.");//because

        // Finally, see that the right message was delivered - EMPTY_REQUEST_EXCEPTION_TEXT
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));

    }

    /** ************************************************************************************ */

/*
    @Test
    @DisplayName("Tests getting metadata with a valid file designator, should return a trio of key sig, time sig and tempo.")
    public void testGetPieceMetadataValidFile() throws InvalidDesignatorException {
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        GetPieceMetadataResponse res = displayService.getPieceMetadata(req);
        logAllFields(res);
    }

    @Test
    @DisplayName("Tests getting track info with a valid file designator, should return a list of instrument lines.")
    public void testGetTrackInfoValidFile() throws InvalidDesignatorException {
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        GetTrackInfoResponse res = displayService.getTrackInfo(req);
        logAllFields(res);
    }

    @Test
    @DisplayName("Tests getting metadata with a valid file designator, should return a trio of key sig, time sig and tempo")
    public void testGetTrackMetadataValidFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);
        GetTrackMetadataResponse res = displayService.getTrackMetadata(req);
        FileWriter myWriter = new FileWriter("GetTrackMetadataSuccess.txt");
        myWriter.write(res.getTrackString());
        myWriter.close();
        logAllFields(res);
    }

    @Test
    @DisplayName("Tests getting metadata with a valid file designator, should return a trio of key sig, time sig and tempo")
    public void testGetPieceOverviewValidFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);
        GetTrackOverviewResponse res = displayService.getTrackOverview(req);
        logAllFields(res);
    }


    @Test
    @DisplayName("Tests getting metadata with an invalid file designator")
    public void testGetPieceMetadataInvalidFile() throws InvalidDesignatorException {
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getPieceMetadata(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting track info with an invalid file designator")
    public void testGetTrackInfoInvalidFile() throws InvalidDesignatorException {
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackInfo(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting metadata with an invalid file designator")
    public void testGetTrackMetadataInvalidFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator),TestingDictionary.display_all_valid_track_index);
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackMetadata(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting metadata with an invalid file designator")
    public void testGetPieceOverviewInvalidFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator),TestingDictionary.display_all_valid_track_index);
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackOverview(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }



    @Test
    @DisplayName("Tests getting metadata with an invalid track index and valid file")
    public void testGetTrackMetadataInvalidIndex() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator),TestingDictionary.display_all_invalid_track_index);
        InvalidTrackException thrown = assertThrows(InvalidTrackException.class,
                ()->displayService.getTrackMetadata(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting metadata with an invalid track index and valid file")
    public void testGetPieceOverviewInvalidIndex() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator), TestingDictionary.display_all_invalid_track_index);
        InvalidTrackException thrown = assertThrows(InvalidTrackException.class,
                () -> displayService.getTrackOverview(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT)));
    }



    @Test
    @DisplayName("Tests getting metadata with an empty file designator")
    public void testGetPieceMetadataEmptyFile() throws InvalidDesignatorException {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getPieceMetadata(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting track info with an empty file designator")
    public void testGetTrackInfoEmptyFile() throws InvalidDesignatorException {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackInfo(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting metadata with an empty file designator")
    public void testGetTrackMetadataEmptyFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackMetadata(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }

    @Test
    @DisplayName("Tests getting metadata with an empty file designator")
    public void testGetPieceOverviewEmptyFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackOverview(null),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }
*/


    
}
