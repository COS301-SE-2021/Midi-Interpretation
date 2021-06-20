package com.noxception.midisense.dataclass;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.MimeType;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestingDictionary {
    //naming convention is <subsystem>_<use case>_<association>

    //Interpreter
    public static byte[] interpreter_uploadFile_validFileContents = getValidByteArray();
    public static byte[] interpreter_uploadFile_invalidFileContents = {};

    private static final String d1 = "9ad37ae5-fde0-407b-80e5-27ec0b6b2bce";
    private static final String d2 = "6698692f-840d-4c30-bcfe-dc544ac345f9";

    public static String interpreter_all_validFileDesignator = d2;
    public static String interpreter_all_invalidFileDesignator = "5698692f-840d-4c30-bcfe-dc544ac345f9";

    public static byte display_all_valid_track_index = 0;
    public static byte display_all_invalid_track_index = 14;

    @Autowired
    private WebApplicationContext webApplicationContext;


    //MISC methods
    public static byte[] getValidByteArray(){
        try {
            return Files.readAllBytes(Paths.get("src/main/java/com/noxception/midisense/midiPool/MyHeartWillGoOn.mid"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MvcResult mockRequest(String subsystem, String useCase, Object request, MockMvc mvc) throws Exception {
        RequestBuilder rq = MockMvcRequestBuilders
                .post("/"+subsystem+"/" + useCase)
                .content(convertToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        return mvc.perform(rq).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    public static MvcResult mockRequestFile(String subsystem, String useCase, String filename, MockMvc mvc) throws Exception {
        MockMultipartFile file = new MockMultipartFile("Tester.mid",Files.readAllBytes(Path.of(filename)));
        RequestBuilder rq = MockMvcRequestBuilders
                .multipart("/"+subsystem+"/" + useCase)
                .content(file.getBytes())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_JSON);
        return mvc.perform(rq).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    private static String convertToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
