package com.noxception.midisense.controller;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.models.InterpreterProcessFileRequest;
import com.noxception.midisense.models.InterpreterUploadFileRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    //checking code and designator is correct

    /**UploadFile*/
    @Test
    @DisplayName("Upload File: input [valid file] expect [correct response code]")
    void test_WhiteBox_UploadFile_IfValidFile_ThenAccurateResponse() throws Exception{


        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        String fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)+testName;

        //Extracting the file contents of the testing file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(new File(fileName).toPath())
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

        //still need to confirm this is valid
        //Assertions.assertEquals(file.getOriginalFilename(),fileName);

        Assertions.assertTrue(new File(fileName).delete());
    }
/*
    @Ignore
    @Test
    @DisplayName("Upload File: input [invalid file] expect [correct response code]")
    void test_WhiteBox_UploadFile_IfInvalidFile_ThenAccurateResponse() throws Exception{

        //create new request, list and byte array
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();

        //Getting the name of the invalid testing file
        String fileContent = configurations.configuration(
                ConfigurationName.MIDI_INVALID_TESTING_FILE
        );

        //Extracting the file contents of the testing file using a byte array
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = fileContent.getBytes();

        //add all bytes in inArray to newByteArray
        for (byte b : inArray) newByteArray.add((int) b);

        //pass into request
        request.setFileContents(newByteArray);

        //mock request
        MvcResult response = mockRequest(
                "interpreter",
                "uploadFile",
                request,
                mvc);

        //check for successful response
        Assertions.assertEquals(415, response.getResponse().getStatus());

        //still need to confirm this is valid
        //Assertions.assertEquals(request.getFileContents().toString(),fileContent);

    }
*/

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


        //still need to confirm this is valid
        //Assertions.assertEquals(request.getFileDesignator(),fileDesignator.toString());

    }
/*
    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Process File: input [valid file] expect [correct response code]")
    void test_WhiteBox_ProcessFile_IfInvalidFileDesignator_ThenAccurateResponse() throws Exception{

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

        //still need to confirm this is valid
        //Assertions.assertEquals(request.getFileDesignator(),fileDesignator.toString());
    }
*/



    //====================================================================================================================//
    //                                  BLACK BOX TESTING BELOW                                                           //
    //====================================================================================================================//
    //Just checking code returned is correct

    /**UploadFile*/
    @Test
    @DisplayName("Upload File: input [valid file] expect [positive integer]")
    void test_BlackBox_UploadFile_IfValidFile_ThenAccurateResponse() throws Exception{

        //Create a temporary file to parse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        String fileName = configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName;

        //Extracting the file contents of the testing file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(new File(fileName).toPath())
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

        Assertions.assertTrue(new File(fileName).delete());    }

    @Ignore
    @Test
    @DisplayName("Upload File: input [invalid file] expect [positive integer]")
    void test_BlackBox_UploadFile_IfInvalidFile_ThenAccurateResponse() throws Exception{

        //create new request, list and byte array
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();

        //Getting the name of the invalid testing file
        String fileContent = configurations.configuration(
                ConfigurationName.MIDI_INVALID_TESTING_FILE
        );

        //Extracting the file contents of the testing file using a byte array
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = fileContent.getBytes();

        //add all bytes in inArray to newByteArray
        for (byte b : inArray) newByteArray.add((int) b);

        //pass into request
        request.setFileContents(newByteArray);

        //mock request
        MvcResult response = mockRequest(
                "interpreter",
                "uploadFile",
                request,
                mvc);

        //check for successful response
        Assertions.assertEquals(415, response.getResponse().getStatus());
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