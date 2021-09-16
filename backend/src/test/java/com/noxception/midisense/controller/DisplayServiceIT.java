package com.noxception.midisense.controller;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.models.DisplayGetPieceMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackInfoRequest;
import com.noxception.midisense.models.DisplayGetTrackMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackOverviewRequest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DisplayServiceIT extends MidiSenseIntegrationTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MIDISenseConfig configurations;


    //====================================================================================================================//
    //                                         WHITE BOX TESTING BELOW                                                    //
    //====================================================================================================================//

    /**GetPieceMetadata*/
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
    public void test_WhiteBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc
        );


        //Check that the key signature is valid
        String keySignature = extractJSONAttribute("keySignature", response.getResponse().getContentAsString());
        String[] keyArray = {"Cbmaj", "Gbmaj", "Dbmaj", "Abmaj", "Ebmaj", "Bbmaj", "Fmaj", "Cmaj", "Gmaj", "Dmaj", "Amaj", "Emaj", "Bmaj", "F#maj", "C#maj", "Abmin", "Ebmin", "Bbmin", "Fmin", "Cmin", "Gmin", "Dmin", "Amin", "Emin", "Bmin", "F#min", "C#min", "G#min", "D#min", "A#min"};
        boolean b = Arrays.asList(keyArray).contains(keySignature);
        assertTrue(b);

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /**GetTrackInfo*/
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [A map consisting of at least 1 track]")
    public void test_WhiteBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc
        );

        //TODO: Confirm the attribute "track_map" is the correct attribute name to get and test
        //Check we receive an array back with at least one entry in it
        String trackMap = extractJSONAttribute("trackName", response.getResponse().getContentAsString());
        assertFalse(trackMap.isEmpty());

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /**GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
    public void test_WhiteBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception{

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get an valid track index
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //TODO: Confirm the attribute "track_string" is the correct attribute name to get and test
        String trackString = response.getResponse().getContentAsString();
        //Check that there is a substring for an inner array with countably many items
        String regex = "\\{\\\"trackString\\\":\\\"\\{\\\\\\\"channel\\\\\\\": ([0-9]|(1[0-5])), \\\\\\\"instrument\\\\\\\": \\\\\\\".+\\\\\\\", \\\\\\\"ticks_per_beat\\\\\\\": ([1-9]([0-9])*), \\\\\\\"track\\\\\\\": \\[(\\{.+\\})*\\]\\}\\\"(,.+)*\\}";
        Pattern validResponse = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = validResponse.matcher(trackString);

        //see that the substring is present
        assertTrue(matcher.find());


        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /**GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
    public void test_WhiteBox_GetTrackOverview_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception {
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get track index too high
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackOverview",
                request,
                mvc
        );

        //TODO: Confirm the attribute "pitch_Array" is the correct attribute name to get and test
        //Check we receive an array back with at least one entry in it
        String s = "";
        String pitchArray = extractJSONAttribute("trackArray", response.getResponse().getContentAsString());
        //Check the array has at least one item
        assertNotEquals(s, pitchArray);

        //check for failed response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }




    //====================================================================================================================//
    //                                           BLACK BOX TESTING BELOW                                                  //
    //====================================================================================================================//


    /*GetPieceMetadata*/
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /*GetPieceMetadata*/
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file not in DB] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfNotInDatabase_ThenException() throws Exception{
        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Get the designator of a file not in the DB
        UUID fileDesignator = UUID.randomUUID();

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetPieceMetadata*/
    @Test
    @DisplayName("Get Piece Metadata: input [empty] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfEmptyRequest_ThenException() throws Exception {

        //make an empty request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //pass into request
        request.setFileDesignator("");

        //make request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }




    /*GetTrackInfo*/
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /*GetTrackInfo*/
    @Test
    @DisplayName("Get Track Info: input [Designator for file not in DB] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfNotInDatabase_ThenException() throws Exception{

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Get the designator of a file not in the DB
        UUID fileDesignator = UUID.randomUUID();

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackInfo*/
    @Test
    @DisplayName("Get Track Info: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfEmptyRequest_ThenException() throws Exception{

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //pass into request
        request.setFileDesignator("");

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }





    /*GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception{

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get an valid track index
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /*GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenAccurateInfo() throws Exception {

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get track index too high
        int trackIndex = 16;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too low)] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenAccurateInfo() throws Exception{
        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get track index too high
        int trackIndex = -1;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfNotInDatabaseAndValidTrack_ThenException() throws Exception{
        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of an invalid file
        UUID fileDesignator = UUID.randomUUID();

        //Get first track index
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and invalid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfNotInDatabaseAndInvalidTrack_ThenException() throws Exception{
        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file not in the DB
        UUID fileDesignator = UUID.randomUUID();

        //Get invalid track index
        int trackIndex = 16;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfEmptyRequest_ThenException() throws Exception{
        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //pass into request
        request.setFileDesignator("");


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }





    /*GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [Designator for file in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackOverview_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception {
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get track index too high
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackOverview",
                request,
                mvc
        );

        //check for response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    /*GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [Designator for file in DB and invalid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackOverview_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenException() throws Exception{
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get track index too high
        int trackIndex = 99999;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [Designator for file in DB and invalid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackOverview_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenException() throws Exception{
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get track index too high
        int trackIndex = -1;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [Designator for file not in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackOverview_IfNotInDatabaseAndValidTrack_ThenException() throws Exception{
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.randomUUID();

        //Get track index too high
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for response is a positive integer
        Assertions.assertEquals(400,response.getResponse().getStatus());

    }

    /*GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [Designator for file not in DB and invalid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackOverview_IfNotInDatabaseAndInvalidTrack_ThenException() throws Exception{
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.randomUUID();

        //Get track index too high
        int trackIndex = 99999;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    /*GetTrackOverview*/
    @Test
    @DisplayName("Get Track Overview: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackOverview_IfEmptyRequest_ThenException() throws Exception{
        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //pass into request
        request.setFileDesignator("");


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }


}
