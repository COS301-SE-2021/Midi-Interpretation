package com.noxception.midisense.controller;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.noxception.midisense.models.IntelligenceAnalyseGenreRequest;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IntelligenceServiceIT extends MidiSenseIntegrationTest{
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MIDISenseConfig configurations;

    /**analyseGenre*/
    @Test
    @DisplayName("Analyse Genre: input [designator for a file in storage] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfValidFileDesignator_ThenAccurateInfo() throws Exception {

        //make a request
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

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
                "intelligence",
                "analyseGenre",
                request,
                mvc
        );


        //check for successful response
        Assertions.assertEquals(200, response.getResponse().getStatus());

        //Delete file from local storage
        File fileToDelete = new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Assertions.assertTrue(fileToDelete.delete());

    }

    @Test
    @DisplayName("Analyse Genre: input [designator for a file not in DB] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfNotValidDesignator_ThenException() throws Exception {

        //make request
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

        //assign fileDesignator to random UUID
        UUID fileDesignator = UUID.randomUUID();

        //pass fileDesignator to request
        request.setFileDesignator(fileDesignator.toString());

        //mock request
        MvcResult response = mockRequest(
                "intelligence",
                "analyseGenre",
                request,
                mvc
        );

        //check for failed response
        Assertions.assertEquals(400, response.getResponse().getStatus());

    }
}
