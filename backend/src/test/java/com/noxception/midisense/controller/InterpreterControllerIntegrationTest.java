package com.noxception.midisense.controller;

import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.models.InterpreterInterpretMetreRequest;
import com.noxception.midisense.models.InterpreterInterpretTempoRequest;
import com.noxception.midisense.models.InterpreterUploadFileRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class InterpreterControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testUploadFileValidFile() throws Exception{
        InterpreterUploadFileRequest request = new InterpreterUploadFileRequest();
        List<Integer> newByteArray = new ArrayList<>();
        byte[] inArray = TestingDictionary.interpreter_uploadFile_validFileContents;
        if(inArray != null) for (byte b : inArray) newByteArray.add((int) b);
        request.setFileContents(newByteArray);
        MvcResult response = TestingDictionary.mockRequest("interpreter","uploadFile",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
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
    public void testInterpretMetreValidFile() throws Exception{
        InterpreterInterpretMetreRequest request = new InterpreterInterpretMetreRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_validFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","interpretMetre",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    public void testInterpretTempoValidFile() throws Exception{
        InterpreterInterpretTempoRequest request = new InterpreterInterpretTempoRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_validFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","interpretTempo",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    public void testInterpretMetreInvalidFile() throws Exception{
        InterpreterInterpretMetreRequest request = new InterpreterInterpretMetreRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_invalidFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","interpretMetre",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    public void testInterpretTempoInvalidFile() throws Exception{
        InterpreterInterpretTempoRequest request = new InterpreterInterpretTempoRequest();
        request.setFileDesignator(TestingDictionary.interpreter_all_invalidFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("interpreter","interpretTempo",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

}