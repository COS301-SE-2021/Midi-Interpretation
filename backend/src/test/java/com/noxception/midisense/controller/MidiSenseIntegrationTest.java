package com.noxception.midisense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@Slf4j
public class MidiSenseIntegrationTest {


    private long maxRequestTime = 2 * 60 * 1000;

    public MvcResult mockRequest(String subsystem, String useCase, Object request, MockMvc mvc, List<ResultMatcher> conditions, long requestTimeout) throws Exception {
        long startTime = System.currentTimeMillis();
        RequestBuilder req = MockMvcRequestBuilders
                .post("/"+subsystem+"/" + useCase)
                .content(convertToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        return handleResponse(mvc,req,useCase,startTime, conditions, maxRequestTime);
    }

    public MvcResult mockRequest(String subsystem, String useCase, Object request, MockMvc mvc) throws Exception {
        return mockRequest(subsystem,useCase,request,mvc,new ArrayList<>(), maxRequestTime);
    }

    public MvcResult mockUpload(String subsystem, String useCase, MockMultipartFile file, MockMvc mvc) throws Exception {
        return mockUpload(subsystem,useCase,file,mvc,new ArrayList<>(), maxRequestTime);
    }

    public MvcResult mockUpload(String subsystem, String useCase, MockMultipartFile file, MockMvc mvc, List<ResultMatcher> conditions, long requestTimeout) throws Exception {
        long startTime = System.currentTimeMillis();
        RequestBuilder req = MockMvcRequestBuilders
                .multipart("/"+subsystem+"/" + useCase)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.ALL);
        return handleResponse(mvc,req,useCase,startTime,conditions,requestTimeout);
    }

    private MvcResult handleResponse(MockMvc mvc, RequestBuilder req, String name, long startTime, List<ResultMatcher> conditions, long requestTimeout) throws Exception {
        ResultActions actions = mvc.perform(req);

        //add all the conditions for a test
        for(ResultMatcher condition : conditions)
            actions.andExpect(condition);

        actions.andExpect((res)->{
            long elapsed = System.currentTimeMillis() - startTime;
            log.info(String.format("Time Elapsed | To: %s | [%s ms]",name,elapsed));
            assertTrue(String.format("Method %s Timeout | Expected to finish within [%s ms] | Finished in [%s ms]",name,requestTimeout,elapsed),(elapsed <= requestTimeout));
        });

        //return the MVC result
        return actions.andReturn();
    }

    private String convertToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String extractJSONAttribute(String attribute, String jsonString){
        String startAttribute = "\""+attribute+"\":";
        String closeAttribute = ",";
        String altCloseAttribute = "}";

        int startIndex = jsonString.indexOf(startAttribute)+startAttribute.length();
        int endIndex = jsonString.indexOf(closeAttribute,startIndex+1);
        int altEndIndex = jsonString.indexOf(closeAttribute,startIndex+1);

        if(startIndex==-1)
            return null;
        else if((startIndex!=-1 && endIndex==-1) && altEndIndex!= -1){
            return jsonString.substring(startIndex,altEndIndex).replace("\"","");
        }
        else{
            return jsonString.substring(startIndex,endIndex).replace("\"","");
        }

    }

}
