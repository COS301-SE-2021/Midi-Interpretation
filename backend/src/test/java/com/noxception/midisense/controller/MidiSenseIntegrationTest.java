package com.noxception.midisense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

public class MidiSenseIntegrationTest {

    public MvcResult mockRequest(String subsystem, String useCase, Object request, MockMvc mvc) throws Exception {
        RequestBuilder rq = MockMvcRequestBuilders
                .post("/"+subsystem+"/" + useCase)
                .content(convertToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        return mvc.perform(rq).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    public MvcResult mockUpload(String subsystem, String useCase, Object request, MockMvc mvc) throws Exception {
        RequestBuilder rq = MockMvcRequestBuilders
                .post("/"+subsystem+"/" + useCase)
                .content((byte[]) request)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.ALL);
        return mvc.perform(rq).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    private String convertToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
