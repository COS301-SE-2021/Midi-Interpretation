package com.noxception.midisense.controller;

import com.jayway.jsonpath.JsonPath;
import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MidiSenseIntegrationTest;
import com.noxception.midisense.intelligence.exceptions.EmptyChordException;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseChordRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseChordResponse;
import com.noxception.midisense.models.IntelligenceAnalyseGenreRequest;
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

import static com.noxception.midisense.dataclass.TestTimeouts.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class that implements testing against the Intelligence Service, both in terms of white and blackbox testing.
 *
 * See {@link com.noxception.midisense.dataclass.MidiSenseIntegrationTest} for an explanation of the mechanisms underlying
 * the actual mock request framework. This class merely illustrates testing against a specific collection of endpoints.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IntelligenceServiceIT extends MidiSenseIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MIDISenseConfig configurations;

    private UUID testDesignator;

    @Transactional
    @Rollback
    @Test
    public void setupFileInDatabase() throws Exception{
        if(testDesignator == null){
            test_WhiteBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse();
        }
    }


    /**analyseGenre*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Analyse Genre: input [designator for a file in storage] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfValidFileDesignator_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

        //Create a temporary file to parse
        UUID fileDesignator = this.testDesignator;

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));


        //make a request
        MvcResult response = mockRequest(
                "intelligence",
                "analyseGenre",
                request,
                mvc,
                conditions,
                maxAnalyseGenreTime

        );

    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Analyse Genre: input [designator for a file not in DB] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfNotValidDesignator_ThenException() throws Exception {

        //make a request
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

        UUID fileDesignator = UUID.randomUUID();

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));

        //make a request
        MvcResult response = mockRequest(
                "intelligence",
                "analyseGenre",
                request,
                mvc,
                conditions,
                maxAnalyseGenreTime

        );

    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Analyse Genre: input [designator for a file not in DB] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfEmptyRequest_ThenException() throws Exception {

        //make a request
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

        //pass valid file designator into request
        request.setFileDesignator("");

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is4xxClientError());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));

        //make a request
        MvcResult response = mockRequest(
                "intelligence",
                "analyseGenre",
                request,
                mvc,
                conditions,
                maxAnalyseGenreTime

        );

    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Analyse Genre: input [designator for a file in storage] expect [genre array]")
    public void test_WhiteBox_AnalyseGenre_IfValidFileDesignator_ThenAccurateInfo() throws Exception {

        setupFileInDatabase();

        //make a request
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

        //Create a temporary file to parse
        UUID fileDesignator = this.testDesignator;

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));
        //genreArray has items
        conditions.add(jsonPath("$.genreArray").isArray());
        conditions.add(jsonPath("$.genreArray").isNotEmpty());

        //make a request
        MvcResult response = mockRequest(
                "intelligence",
                "analyseGenre",
                request,
                mvc,
                conditions,
                maxAnalyseGenreTime

        );

    }

    @Test
    @DisplayName("Analyse Chord: input [Valid Byte Stream] expect [chord analysis]")
    public void testBlackBox_AnalyseChord_IfValidByteStream() throws Exception {

        //valid byte stream
        byte[] validStream = new byte[]{3, 6, 9, 12};

        //test with request response
        AnalyseChordRequest request = new AnalyseChordRequest(validStream);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());
        //for a json response
        conditions.add(content().contentType(MediaType.APPLICATION_JSON));

        //make a request
        MvcResult response = mockRequest(
                "intelligence",
                "analyseChord",
                request,
                mvc,
                conditions,
                maxAnalyseGenreTime

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
                maxUploadFileTime
        );

        String designator = JsonPath.read(response.getResponse().getContentAsString(), "$.fileDesignator");
        this.testDesignator = UUID.fromString(designator);

        fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+designator+configurations.configuration(ConfigurationName.FILE_FORMAT);
        File fileToDelete = new File(fileName);
        Assertions.assertTrue(fileToDelete.delete());
    }


    /**ProcessFile*/
    @Test
    @Transactional
    @Rollback
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
                maxProcessFileTime
        );

    }

}
