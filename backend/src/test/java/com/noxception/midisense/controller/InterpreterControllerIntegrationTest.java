package com.noxception.midisense.controller;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.models.InterpreterInterpretMetreRequest;
import com.noxception.midisense.models.InterpreterInterpretTempoRequest;
import com.noxception.midisense.models.InterpreterProcessFileRequest;
import com.noxception.midisense.models.InterpreterUploadFileRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class InterpreterControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Tests uploading a valid file")
    @Tag(MIDISenseUnitTest.TestTags.VALID_INPUT)
    void testUploadFileValidFile() throws Exception{
        MvcResult response = TestingDictionary.mockRequestFile("interpreter","uploadFile","src/main/java/com/noxception/midisense/midiPool/MyHeartWillGoOn.mid", mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests uploading an invalid file")
    @Tag(MIDISenseUnitTest.TestTags.MALFORMED_INPUT)
    void testUploadFileInvalidFile() throws Exception{
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = TestingDictionary.interpreter_uploadFile_invalidFileContents;
        if(inArray != null) for (byte b : inArray) newByteArray.add((int) b);
        request.setFileContents(newByteArray);
        MvcResult response = TestingDictionary.mockRequest("interpreter","uploadFile",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Tests processing a valid file")
    @Tag(MIDISenseUnitTest.TestTags.VALID_INPUT)
    void testProcessFileValidFileDesignator() throws Exception{
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_validFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","processFile",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Tests processing an invalid file")
    @Tag(MIDISenseUnitTest.TestTags.MALFORMED_INPUT)
    void testProcessFileInvalidFileDesignator() throws Exception{
        InterpreterProcessFileRequest request = new InterpreterProcessFileRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_invalidFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","processFile",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }





}