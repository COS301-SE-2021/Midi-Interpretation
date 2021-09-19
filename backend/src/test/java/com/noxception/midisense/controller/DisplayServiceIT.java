package com.noxception.midisense.controller;

import com.jayway.jsonpath.JsonPath;
import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MidiSenseIntegrationTest;
import com.noxception.midisense.models.DisplayGetPieceMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackInfoRequest;
import com.noxception.midisense.models.DisplayGetTrackMetadataRequest;
import com.noxception.midisense.models.InterpreterProcessFileRequest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
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

import static com.noxception.midisense.dataclass.TestTimeouts.maxDisplayTime;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class that implements testing against the Display Service, both in terms of white and blackbox testing.
 *
 * See {@link com.noxception.midisense.dataclass.MidiSenseIntegrationTest} for an explanation of the mechanisms underlying
 * the actual mock request framework. This class merely illustrates testing against a specific collection of endpoints.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DisplayServiceIT extends MidiSenseIntegrationTest {

    //Services and configs
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MIDISenseConfig configurations;

    //Testing Constants
    private UUID testDesignator;
    //regex for track string - need not be as comprehensive as the unit test
    private final String validTrackPattern = "\\{\\\"channel\\\":([0-9]|(1[0-5])),\\\"instrument\\\":\\\"(.){1,30}\\\",\\\"ticks_per_beat\\\":([1-9]([0-9])*),\\\"track\\\":\\[.+\\]\\}";

    //Method to setup a file in the DB
    @Transactional
    @Rollback
    @Test
    public void setupFileInDatabase() throws Exception{
        if(testDesignator == null){
            test_WhiteBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse();
        }
    }


    //====================================================================================================================//
    //                                         WHITE BOX TESTING BELOW                                                    //
    //====================================================================================================================//

    /**GetPieceMetadata*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [beat value a positive power of 2, beat number a positive integer]")
    public void test_WhiteBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

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
                maxDisplayTime
        );

    }

    /**GetTrackInfo*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [A map consisting of at least 1 track]")
    public void test_WhiteBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

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
                maxDisplayTime
        );
    }

    /**GetTrackMetadata*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [array consisting of metadata of 1 track]")
    public void test_WhiteBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

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
        conditions.add(jsonPath("$.trackString").isNotEmpty());


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );

        String trackString = JsonPath.read(response.getResponse().getContentAsString(), "$.trackString");
        //Check that there is a substring for an inner array with countably many items
        Pattern validResponse = Pattern.compile(this.validTrackPattern,Pattern.MULTILINE);
        Matcher matcher = validResponse.matcher(trackString);

        //see that the substring is present
        assertTrue(matcher.find());

    }




    //====================================================================================================================//
    //                                           BLACK BOX TESTING BELOW                                                  //
    //====================================================================================================================//


    /**GetPieceMetadata*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file in DB] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Piece Metadata: input [designator for a file not in DB] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfNotInDatabase_ThenException() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //Getting the designator of a file not in the DB
        UUID fileDesignator = UUID.randomUUID();

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Piece Metadata: input [empty] expect [positive integer]")
    public void test_BlackBox_GetPieceMetadata_IfEmptyRequest_ThenException() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();

        //set the designator to nothing
        request.setFileDesignator("");

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getPieceMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }


    /**GetTrackInfo*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Info: input [Designator for file in DB] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfPresentInDatabase_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Info: input [Designator for file not in DB] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfNotInDatabase_ThenException() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //Getting the designator of a file not in the DB
        UUID fileDesignator = UUID.randomUUID();

        //pass into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Info: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackInfo_IfEmptyRequest_ThenException() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();

        //set the designator to nothing
        request.setFileDesignator("");


        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());


        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackInfo",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }


    /**GetTrackMetadata*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithValidTrackAndValidID_ThenAccurateInfo() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

        //Get an valid track index
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too high)] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooHighAndInvalidID_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

        //Get an invalid track index (too high)
        int trackIndex = 16;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file in DB and invalid track index (too low)] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfPresentInDatabaseWithInvalidTrackTooLowAndInvalidID_ThenAccurateInfo() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = this.testDesignator;

        //Get an invalid track index (too low)
        int trackIndex = -1;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and valid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfNotInDatabaseAndValidTrack_ThenException() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file not in the DB
        UUID fileDesignator = UUID.randomUUID();

        //Get an valid track index
        int trackIndex = 0;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [Designator for file not in DB and invalid track index] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfNotInDatabaseAndInvalidTrack_ThenException() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //Getting the designator of a file in the DB
        UUID fileDesignator = UUID.randomUUID();

        //Get an invalid track index
        int trackIndex = -1;

        //pass into request
        request.setFileDesignator(fileDesignator.toString());
        request.setTrackIndex(trackIndex);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Get Track Metadata: input [empty] expect [positive integer]")
    public void test_BlackBox_GetTrackMetadata_IfEmptyRequest_ThenException() throws Exception{

        setupFileInDatabase();

        //make a request
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();

        //set the designator to nothing
        request.setFileDesignator("");

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());

        //make a request
        MvcResult response = mockRequest(
                "display",
                "getTrackMetadata",
                request,
                mvc,
                conditions,
                maxDisplayTime
        );
    }


    /**UploadFile*/
    @Transactional
    @Rollback
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
                maxDisplayTime
        );

        String designator = JsonPath.read(response.getResponse().getContentAsString(), "$.fileDesignator");
        this.testDesignator = UUID.fromString(designator);

        fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+designator+configurations.configuration(ConfigurationName.FILE_FORMAT);
        File fileToDelete = new File(fileName);
        Assertions.assertTrue(fileToDelete.delete());
    }


    /**ProcessFile*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Process File: input [valid file] expect [correct response code]")
    public void test_WhiteBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse() throws Exception{

        //mock the uploading of a file
        test_WhiteBox_UploadFile_IfValidFile_ThenAccurateResponse();

        //Get a designator
        UUID fileDesignator = this.testDesignator;

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
