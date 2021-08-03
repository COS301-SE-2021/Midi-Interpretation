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
    @Test
    public void test_GetPieceMetaData_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator));
        GetPieceMetadataResponse res = displayService.getPieceMetadata(req);

        //key sig
        String[] keyArray = {"Cbmaj", "Gbmaj", "Dbmaj", "Abmaj", "Ebmaj", "Bbmaj", "Fmaj", "Cmaj", "Gmaj", "Dmaj", "Amaj", "Emaj", "Bmaj", "F#maj", "C#maj", "Abmin", "Ebmin", "Bbmin", "Fmin", "Cmin", "Gmin", "Dmin", "Amin", "Emin", "Bmin", "F#min", "C#min", "G#min", "D#min", "A#min"};
        boolean b = Arrays.asList(keyArray).contains(res.getKeySignature().getSignatureName());
        assertTrue(b);

        //tempo
        assertTrue(res.getTempoIndication().getTempo()>0);


        // beat val for time sig
        int beatValue = res.getTimeSignature().getBeatValue();
        double c = Math.log(beatValue)/Math.log(2);
        assertEquals(c,Math.floor(c));


        // beat num for time sig
        int numBeats = res.getTimeSignature().getNumBeats();
        assertTrue(numBeats>0);

    }

    @Test
    public void test_GetPieceMetaData_IfNotInDatabase_ThenException() {
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getPieceMetadata(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    public void test_GetPieceMetaData_IfEmptyRequest_ThenException() {
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                ()->displayService.getPieceMetadata(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }


    /**GetTrackInfo*/
    @Test
    public void test_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator));
        GetTrackInfoResponse res = displayService.getTrackInfo(req);
        assertFalse(res.getTrackMap().isEmpty());
    }

    @Test
    public void test_GetTrackInfo_IfNotInDatabase_ThenException() {
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.display_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->displayService.getTrackInfo(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT)));
    }

    @Test
    public void test_GetTrackInfo_IfEmptyRequest_ThenException() {
        InvalidUploadException thrown = assertThrows(InvalidUploadException.class,
                ()->displayService.getTrackInfo(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT)));
    }


    /**GetTrackMetadata*/
    @Test
    public void test_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException {
        //Make request
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);

        //Get response
        GetTrackMetadataResponse res = displayService.getTrackMetadata(req);

        //Check that the substring is in the Track Metadata
        assertTrue(res.getTrackString().contains("trackString"));
    }

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
    @Test
    public void test_GetTrackOverview_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException {
        //Make request
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.display_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);

        //Get a response
        GetTrackOverviewResponse res = displayService.getTrackOverview(req);

        //Check the array has at least one item
        assertFalse(res.getPitchArray().isEmpty());
    }

    @Test
    public void test_GetTrackOverview_IfPresentInDatabaseWithInValidTrackAndInvalidID_ThenAccurateInfo() {
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
