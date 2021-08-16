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
    @DisplayName("Analyse Genre: input [designator for a file in DB] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfPresentInDatabase_ThenAccurateInfo() throws Exception {
        IntelligenceAnalyseGenreRequest request = new IntelligenceAnalyseGenreRequest();

        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);


        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);


        request.setFileDesignator(fileDesignator.toString());


        MvcResult response = mockRequest(
                "intelligence",
                "analyseGenre",
                request,
                mvc
        );



        Assertions.assertEquals(200, response.getResponse().getStatus());


        File fileToDelete = new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Assertions.assertTrue(fileToDelete.delete());

    }

    @Test
    @DisplayName("Analyse Genre: input [designator for a file not in DB] expect [genre array]")
    public void test_BlackBox_AnalyseGenre_IfPresentNotInDatabase_ThenAccurateInfo() throws Exception {


    }
}
