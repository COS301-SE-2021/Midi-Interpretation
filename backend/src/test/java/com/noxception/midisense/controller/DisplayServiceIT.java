package com.noxception.midisense.controller;

import com.jayway.jsonpath.JsonPath;
import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.models.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DisplayServiceIT extends MidiSenseIntegrationTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MIDISenseConfig configurations;

    private UUID fileDesignator;



    @Test
    public void setupFileInDatabase() throws Exception{
        if(fileDesignator == null){
            test_WhiteBox_UploadFile_IfValidFile_ThenAccurateResponse();
            test_WhiteBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse();
        }
    }


    //====================================================================================================================//
    //                                         WHITE BOX TESTING BELOW                                                    //
    //====================================================================================================================//

    /**GetPieceMetadata*/
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
    public void test_WhiteBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));
        //check that there is a message and success
        conditions.add(jsonPath("$.message").exists());
        conditions.add(jsonPath("$.success").exists());
        //check that there is a message and success
        conditions.add(jsonPath("$.success").value(true));
        //check if the arrays are there and non-empty
        conditions.add(jsonPath("$.keySignatureMap").isArray());
        conditions.add(jsonPath("$.keySignatureMap").isNotEmpty());
        conditions.add(jsonPath("$.timeSignatureMap").isArray());
        conditions.add(jsonPath("$.timeSignatureMap").isNotEmpty());
        conditions.add(jsonPath("$.tempoIndicationMap").isArray());
        conditions.add(jsonPath("$.tempoIndicationMap").isNotEmpty());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc,
                conditions,
                1000
        );

    }

    /**GetTrackInfo*/
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [A map consisting of at least 1 track]")
    public void test_WhiteBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));
        //check if the arrays are there and non-empty
        conditions.add(jsonPath("$").isArray());
        conditions.add(jsonPath("$").isNotEmpty());


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc,
                conditions,
                1000
        );
    }

    /**GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
    public void test_WhiteBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

        //Get an valid track index
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));
        //check that there is a message and success
        conditions.add(jsonPath("$.message").exists());
        conditions.add(jsonPath("$.success").exists());
        //check that there is a message and success
        conditions.add(jsonPath("$.success").value(true));
        //check if the arrays are there and non-empty
        
        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                1000
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

        setupFileInDatabase();

        //make a request
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

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


    /**GetPieceMetadata*/
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

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

    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file not in DB] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfNotInDatabase_ThenException() throws Exception{

        setupFileInDatabase();

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

    @Test
    @DisplayName("Get Piece Metadata: input [empty] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfEmptyRequest_ThenException() throws Exception {

        setupFileInDatabase();

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


    /**GetTrackInfo*/
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

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

    @Test
    @DisplayName("Get Track Info: input [Designator for file not in DB] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfNotInDatabase_ThenException() throws Exception{

        setupFileInDatabase();

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

    @Test
    @DisplayName("Get Track Info: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfEmptyRequest_ThenException() throws Exception{

        setupFileInDatabase();

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


    /**GetTrackMetadata*/
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

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

    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

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

    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too low)] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenAccurateInfo() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.fileDesignator;

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

    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfNotInDatabaseAndValidTrack_ThenException() throws Exception{

        setupFileInDatabase();

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

    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and invalid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfNotInDatabaseAndInvalidTrack_ThenException() throws Exception{

        setupFileInDatabase();

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

    @Test
    @DisplayName("Get Track Metadata: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfEmptyRequest_ThenException() throws Exception{

        setupFileInDatabase();

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


    /**UploadFile*/
    @Test
    @DisplayName("Upload File: input [valid file] expect [correct response code]")
    public void test_WhiteBox_UploadFile_IfValidFile_ThenAccurateResponse() throws Exception{

        String fileName = configurations.configuration(ConfigurationName.MIDI_TESTING_FILE);
        File testfile = new File(fileName);

        //Extracting the file contents of the testing file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(testfile.toPath())
        );

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));
        //check that a file designator exists and is a UUID
        conditions.add(jsonPath("$.fileDesignator").exists());

        //mock request
        MvcResult response = mockUpload(
                //To the interpreter subsystem
                "interpreter",
                //To upload a file
                "uploadFile",
                //With these contents
                file,
                //To the MVC
                mvc,
                //With these test conditions
                conditions,
                //With this timeout (in ms)
                1000
        );

        String designator = JsonPath.read(response.getResponse().getContentAsString(), "$.fileDesignator");
        this.fileDesignator = UUID.fromString(designator);
    }


    /**ProcessFile*/
    @Test
    @Transactional
    @DisplayName("Process File: input [valid file] expect [correct response code]")
    public void test_WhiteBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse() throws Exception{

        //Get a designator
        UUID fileDesignator = this.fileDesignator;

        //create request object
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));
        //check that there is a message and success
        conditions.add(jsonPath("$.message").exists());
        conditions.add(jsonPath("$.success").exists());
        //check that there is a message and success
        conditions.add(jsonPath("$.success").value(true));

        //make a request
        MvcResult response = mockRequest(
                //To the interpreter subsystem
                "interpreter",
                //To upload a file
                "processFile",
                //With this request
                request,
                //Against the mock MVC
                mvc,
                //With these conditions
                conditions,
                //With this timeout (in ms)
                15000
        );

    }


}
