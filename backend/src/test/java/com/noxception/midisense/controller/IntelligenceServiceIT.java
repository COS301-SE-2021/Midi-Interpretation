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


    }

}
