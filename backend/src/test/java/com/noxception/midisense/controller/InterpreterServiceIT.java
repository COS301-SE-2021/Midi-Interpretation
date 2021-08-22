package com.noxception.midisense.controller;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.models.InterpreterProcessFileRequest;
import org.junit.Ignore;
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

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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
        //mock request
        MvcResult response = mockUpload(
                "interpreter",
                "uploadFile",
                file,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());

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

        //make a request
        MvcResult response = mockRequest(
                "interpreter",
                "processFile",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());


        File fileToDelete = new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Assertions.assertTrue(fileToDelete.delete());

    }




    //====================================================================================================================//
    //                                  BLACK BOX TESTING BELOW                                                           //
    //====================================================================================================================//
    //checking returned code returned is correct

    /**UploadFile*/
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
        //mock request
        MvcResult response = mockUpload(
                "interpreter",
                "uploadFile",
                file,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());

        String fileDesignatorToDelete = extractJSONAttribute("fileDesignator",response.getResponse().getContentAsString());
        fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+fileDesignatorToDelete+configurations.configuration(ConfigurationName.FILE_FORMAT);
        File fileToDelete = new File(fileName);
        fileToDelete.delete();
        //Assertions.assertTrue(fileToDelete.delete());
    }

    @Ignore
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
        //mock request
        MvcResult response = mockUpload(
                "interpreter",
                "uploadFile",
                file,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(400, response.getResponse().getStatus());


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

        //pass valid file designator into request
        request.setFileDesignator(fileDesignator.toString());

        //make a request
        MvcResult response = mockRequest(
                "interpreter",
                "processFile",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());

        String fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+testName;
        File fileToDelete = new File(fileName);
        fileToDelete.delete();
        //Assertions.assertTrue(fileToDelete.delete());
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

        //make a request
        MvcResult response = mockRequest(
                "interpreter",
                "processFile",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }


}