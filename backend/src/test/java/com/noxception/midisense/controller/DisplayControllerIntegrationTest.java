package com.noxception.midisense.controller;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.models.DisplayGetPieceMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackInfoRequest;
import com.noxception.midisense.models.DisplayGetTrackMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackOverviewRequest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DisplayControllerIntegrationTest extends MidiSenseIntegrationTest{

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Tests getting piece metadata with a valid file designator")
    public void testGetPieceMetadataValidDesignator() throws Exception {

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track info with a valid file designator")
    public void testGetTrackInfoValidDesignator() throws Exception {

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track metadata with a valid file designator")
    public void testGetTrackMetadataValidDesignator() throws Exception {

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(validTrackIndex);

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc);

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track overview with a valid file designator")
    public void testGetTrackOverviewValidDesignator() throws Exception {

        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(validTrackIndex);

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackOverview",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting piece metadata with an invalid file designator")
    public void testGetPieceMetadataInvalidDesignator() throws Exception {

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //generate an invalid designator
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

    @Test
    @DisplayName("Tests getting track info with an invalid file designator")
    public void testGetTrackInfoInvalidDesignator() throws Exception {

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //generate an invalid designator
        UUID fileDesignator = UUID.randomUUID();

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track metadata with an invalid file designator")
    public void testGetTrackMetadataInvalidDesignator() throws Exception {

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //generate an invalid designator
        UUID fileDesignator = UUID.randomUUID();

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(validTrackIndex);

        //make request
        MvcResult response = mockRequest("display","getTrackMetadata",request, mvc);

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track overview with an invalid file designator")
    public void testGetTrackOverviewInvalidDesignator() throws Exception {

        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //generate an invalid designator
        UUID fileDesignator = UUID.randomUUID();

        //Get a valid track index
        int validTrackIndex = Integer.parseInt(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_TRACK_INDEX
        ));

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(validTrackIndex);

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackOverview",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track metadata with an invalid track index and valid file")
    public void testGetTrackMetadataInvalidTrackIndex() throws Exception {

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //generate an invalid designator
        UUID fileDesignator = UUID.randomUUID();

        //Get an invalid track index
        int trackIndex = 16;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track overview with an invalid track index and valid file")
    public void testGetTrackOverviewInvalidTrackIndex() throws Exception {

        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(MIDISenseConfig.configuration(
                MIDISenseConfig.ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

        //Get an invalid track index
        int trackIndex = 16;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //make request
        MvcResult response = mockRequest(
                "display",
                "getTrackOverview",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    //TODO: ============ NEW FRAMEWORK TO IMPLEMENT ======================

//    @Test
//    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
//    public void test_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {
//
//    }
//
//    @Test
//    @DisplayName("Get Piece Metadata: input [designator for a file not in DB] expect [file does not exist exception]")
//    public void test_GetPieceMetadata_IfNotInDatabase_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Piece Metadata: input [empty] expect [empty request exception]")
//    public void test_GetPieceMetadata_IfEmptyRequest_ThenException() {
//
//    }
//
//
//    @Test
//    @DisplayName("Get Track Info: input [Designator for file in DB] expect [A map consisting of at least 1 track]")
//    public void test_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws InvalidDesignatorException {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Info: input [Designator for file not in DB] expect [file does not exist exception]")
//    public void test_GetTrackInfo_IfNotInDatabase_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Info: input [empty] expect [empty request exception]")
//    public void test_GetTrackInfo_IfEmptyRequest_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
//    public void test_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo(){
//
//    }
//
//    @Test
//    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [invalid track index exception]")
//    public void test_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenAccurateInfo() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [invalid track index exception]")
//    public void test_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenAccurateInfo() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Metadata: input [Designator for file not in DB and valid track index] expect [invalid designator exception]")
//    public void test_GetTrackMetadata_IfNotInDatabaseAndValidTrack_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Metadata: input [Designator for file not in DB and invalid track index] expect [invalid designator and invalid track index exceptions]")
//    public void test_GetTrackMetadata_IfNotInDatabaseAndInvalidTrack_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Metadata: input [empty] expect [empty request exception]")
//    public void test_GetTrackMetadata_IfEmptyRequest_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Overview: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
//    public void test_GetTrackOverview_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws InvalidDesignatorException, InvalidTrackException {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Overview: input [Designator for file in DB and invalid track index] expect [invalid track index exception]")
//    public void test_GetTrackOverview_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Overview: input [Designator for file in DB and invalid track index] expect [invalid track index exception]")
//    public void test_GetTrackOverview_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Overview: input [Designator for file not in DB and valid track index] expect [invalid designator exception]")
//    public void test_GetTrackOverview_IfNotInDatabaseAndValidTrack_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Overview: input [Designator for file not in DB and invalid track index] expect [invalid designator and invalid track index exceptions]")
//    public void test_GetTrackOverview_IfNotInDatabaseAndInvalidTrack_ThenException() {
//
//    }
//
//    @Test
//    @DisplayName("Get Track Overview: input [empty] expect [empty request exception]")
//    public void test_GetTrackOverview_IfEmptyRequest_ThenException() {
//    }


    //=================================================
    
}
