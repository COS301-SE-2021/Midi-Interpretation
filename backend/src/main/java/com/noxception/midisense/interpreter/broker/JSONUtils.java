package com.noxception.midisense.interpreter.broker;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JSONUtils {
    //convert JSON into List of Objects
    public static <T> List<T> JSONToList(String json, TypeReference<List<T>> var) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, var);
    }

    //Generic Type Safe Method – convert JSON into Object
    public static <T> T JSONToObject(String json, Class<T> var) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, var);//Convert Json into object of Specific Type
    }

    //convert Object into JSON
    public static String ObjectToJSON(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

}
