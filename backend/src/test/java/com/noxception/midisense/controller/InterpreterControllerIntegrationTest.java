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

    //TODO: Implement new framework

    @Ignore
    @Test
    @DisplayName("Tests uploading a valid file")
    void testUploadFileValidFile() throws Exception{
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        request.setFileContents(Arrays.asList(1, 2, 3));
        MvcResult response = mockRequest("interpreter","uploadFile",request, mvc);
        Assertions.assertEquals(415, response.getResponse().getStatus());
    }

    @Ignore
    @Test
    @DisplayName("Tests uploading an invalid file")
    void testUploadFileInvalidFile() throws Exception{
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = new byte[]{};
        if(inArray != null) for (byte b : inArray) newByteArray.add((int) b);
        request.setFileContents(newByteArray);
        MvcResult response = mockRequest("interpreter","uploadFile",request, mvc);
        Assertions.assertEquals(415, response.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing a valid file")
    void testProcessFileValidFileDesignator() throws Exception{
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();

        request.setFileDesignator(TestingDictionary.interpreter_all_validFileDesignator);
        MvcResult response = mockRequest("interpreter","processFile",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("Tests processing an invalid file")
    void testProcessFileInvalidFileDesignator() throws Exception{
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_invalidFileDesignator);
        MvcResult response = mockRequest("interpreter","processFile",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }





}