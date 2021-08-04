package com.noxception.midisense.controller;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.models.InterpreterProcessFileRequest;
import com.noxception.midisense.models.InterpreterUploadFileRequest;
import org.json.JSONObject;
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
import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class InterpreterControllerIntegrationTest extends MidiSenseIntegrationTest{

    @Autowired
    private MockMvc mvc;

    @Transactional
    @Rollback(value = true)
    @Test
    void test_UploadFile_IfValidFileContents_ThenPresentDesignator() throws Exception {

        byte[] validFile = new byte[]{1,2,3,4,5};

        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();

        //Generate a list of integer that corresponds to valid byte stream.
        List<Integer> fileContents = new ArrayList<>();
        for(byte b: validFile)
            fileContents.add((int) b);

        request.setFileContents(fileContents);
        MvcResult result = mockRequest("interpreter","uploadFile",request, mvc);

        //Successful request
        Assertions.assertEquals(200,result.getResponse().getStatus());

        //TODO: Want a designator
        List<Integer> contents = request.getFileContents();
        StringBuilder des = new StringBuilder();
        Iterator<Integer> i = contents.iterator();
        while(i.hasNext()){
            des.append(i.next());
        }
        String designator = des.toString();

        //Check to see whether is valid
        UUID fileDesignator = UUID.fromString(designator);

        //check that the resultant file can be opened : was saved to the right directory
        String filename = MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT)
                +fileDesignator
                +MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);
        File newlyCreated = new File(filename);

        //check to see that the file contents are the same
        byte[] newContents = Files.readAllBytes(newlyCreated.toPath());
        assertArrayEquals(newContents,validFile);

        newlyCreated = new File(filename);
        //delete the file
        assertTrue(newlyCreated.delete());

    }


    @Ignore
    @Test
    @DisplayName("Tests uploading a valid file")
    void testUploadFileValidFile() throws Exception{
        //TODO: USE SWAGGER UI FOR NOW - FIGURE OUT WHY HTTP 415
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        request.setFileContents(Arrays.asList(1, 2, 3));
        MvcResult response = TestingDictionary.mockRequest("interpreter","uploadFile",request, mvc);
        Assertions.assertEquals(415, response.getResponse().getStatus());
    }

    @Ignore
    @Test
    @DisplayName("Tests uploading an invalid file")
    void testUploadFileInvalidFile() throws Exception{
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        if(inArray != null) for (byte b : inArray) newByteArray.add((int) b);
        request.setFileContents(newByteArray);
        MvcResult response = TestingDictionary.mockRequest("interpreter","uploadFile",request, mvc);
        Assertions.assertEquals(415, response.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing a valid file")
    void testProcessFileValidFileDesignator() throws Exception{
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_validFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","processFile",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing an invalid file")
    void testProcessFileInvalidFileDesignator() throws Exception{
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_invalidFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","processFile",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }





}