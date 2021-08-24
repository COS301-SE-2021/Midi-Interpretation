package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseChordRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseChordResponse;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;
import com.noxception.midisense.intelligence.strategies.DecisionTreeChordAnalysisStrategy;
import com.noxception.midisense.intelligence.strategies.NeuralNetworkGenreAnalysisStrategy;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class IntelligenceServiceTest extends MIDISenseUnitTest {

    private IntelligenceServiceImpl intelligenceService;
    private StandardConfig configurations;

    @BeforeEach
    public void mountModule(){
        configurations = new MockConfigurationSettings();
        intelligenceService = new IntelligenceServiceImpl(configurations);
        intelligenceService.attachGenreStrategy(new NeuralNetworkGenreAnalysisStrategy(configurations));
    }

    @Test
    public void test_AnalyseGenre_IfValidByteStream_ThenExpectPredictions() throws InvalidDesignatorException, MissingStrategyException, IOException {

        //Create a temporary file to analyse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + configurations.configuration(ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        AnalyseGenreRequest request = new AnalyseGenreRequest(fileDesignator);
        AnalyseGenreResponse response = intelligenceService.analyseGenre(request);
        assertTrue(response.getGenreArray().length > 0);

        //delete the temporary file
        assertTrue(new File(configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());
    }

    //TODO: WHITE AND BLACKBOX TESTING FOR CLASSIFICATIONS
    @Test
    public void basicClassificationTest() throws MissingStrategyException {
        AnalyseChordRequest request = new AnalyseChordRequest(new byte[]{53, 59, 62, 65, 68});
        intelligenceService.attachChordStrategy(new DecisionTreeChordAnalysisStrategy());
        AnalyseChordResponse response = intelligenceService.analyseChord(request);
        System.out.println(response.getChord());
    }


}
