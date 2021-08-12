package com.noxception.midisense.controller;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.TestingDictionary;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class InterpreterServiceIT extends MidiSenseIntegrationTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MIDISenseConfig configurations;


    @Test
    @DisplayName("Tests uploading a valid file")
    void testUploadFileValidFile() throws Exception{

        //make a request
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();

        String fileContent = configurations.configuration(
                ConfigurationName.MIDI_TESTING_FILE
        );

        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = fileContent.getBytes();

        for (byte b : inArray) newByteArray.add((int) b);

        //pass into request
        request.setFileContents(newByteArray);

        //mock request
        MvcResult response = mockRequest(
                "interpreter",
                "uploadFile",
                request,
                mvc
        );

        //check for successful response
        Assertions.assertEquals(415, response.getResponse().getStatus());
    }

    @Ignore
    @Test
    @DisplayName("Tests uploading an invalid file")
    void testUploadFileInvalidFile() throws Exception{

        //create new request, list and byte array
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = new byte[]{};

        //add all bytes in inArray to newByteArray
        if(inArray != null) for (byte b : inArray) newByteArray.add((int) b);

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

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing a valid file")
    void testProcessFileValidFileDesignator() throws Exception{

        //create request object
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();

        //Get the designator of a file in the DB
        UUID fileDesignator = UUID.fromString(configurations.configuration(
                ConfigurationName.MIDI_TESTING_DESIGNATOR
        ));

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
    @DisplayName("Tests processing an invalid file")
    void testProcessFileInvalidFileDesignator() throws Exception{

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