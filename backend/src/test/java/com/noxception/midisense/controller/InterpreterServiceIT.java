package com.noxception.midisense.controller;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.models.InterpreterProcessFileRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static com.noxception.midisense.controller.TestTimeouts.maxProcessFileTime;
import static com.noxception.midisense.controller.TestTimeouts.maxUploadFileTime;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class InterpreterServiceIT extends MidiSenseIntegrationTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MIDISenseConfig configurations;


    //====================================================================================================================//
    //                                  WHITE BOX TESTING BELOW                                                           //
    //====================================================================================================================//
    //checking returned code and designator is correct

    /**UploadFile*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Upload File: input [valid file] expect [correct response code]")
    void test_WhiteBox_UploadFile_IfValidFile_ThenAccurateResponse() throws Exception{

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

        //delete the file used to test
        String fileDesignatorToDelete = extractJSONAttribute("fileDesignator",response.getResponse().getContentAsString());
        fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignatorToDelete+configurations.configuration(ConfigurationName.FILE_FORMAT);
        File fileToDelete = new File(fileName);
        Assertions.assertTrue(fileToDelete.delete());
    }


    /**ProcessFile*/
    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Process File: input [valid file] expect [correct response code]")
    void test_WhiteBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse() throws Exception{

        //create request object
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
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



    //====================================================================================================================//
    //                                  BLACK BOX TESTING BELOW                                                           //
    //====================================================================================================================//
    //checking returned code returned is correct

    /**UploadFile*/
    @Transactional
    @Rollback
    @Test
    @DisplayName("Upload File: input [valid file] expect [positive integer]")
    void test_BlackBox_UploadFile_IfValidFile_ThenAccurateResponse() throws Exception{

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

        //delete the file used to test
        String fileDesignatorToDelete = extractJSONAttribute("fileDesignator",response.getResponse().getContentAsString());
        fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignatorToDelete+configurations.configuration(ConfigurationName.FILE_FORMAT);
        File fileToDelete = new File(fileName);
        Assertions.assertTrue(fileToDelete.delete());

    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("Upload File: input [invalid file] expect [positive integer]")
    void test_BlackBox_UploadFile_IfInvalidFile_ThenAccurateResponse() throws Exception{
        String fileName = configurations.configuration(ConfigurationName.MIDI_INVALID_TESTING_FILE);
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

        //expect 400 response code
        conditions.add(status().is4xxClientError());

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

    }


    /**ProcessFile*/
    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Process File: input [valid file] expect [positive integer]")
    void test_BlackBox_ProcessFile_IfValidFileDesignator_ThenAccurateResponse() throws Exception{

        //create request object
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //expect 200 response code
        conditions.add(status().is2xxSuccessful());

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

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Process File: input [valid file] expect [positive integer]")
    void test_BlackBox_ProcessFile_IfInvalidFileDesignator_ThenAccurateResponse() throws Exception{

        //create request object
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.randomUUID();

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //specify condition of request
        List<ResultMatcher> conditions = new ArrayList<>();

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //expect 400 response code
        conditions.add(status().is4xxClientError());

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