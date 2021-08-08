package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;
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

import static org.junit.jupiter.api.Assertions.assertTrue;



public class IntelligenceServiceImplTest extends MIDISenseUnitTest {

    IntelligenceServiceImpl intelligenceService;

    @BeforeEach
    public void mountModule(){
        intelligenceService = new IntelligenceServiceImpl();
        intelligenceService.attachGenreStrategy(new NeuralNetworkGenreAnalysisStrategy());
    }

    @Test
    public void test_AnalyseGenre_IfValidByteStream_ThenExpectPredictions() throws InvalidDesignatorException, MissingStrategyException, IOException {

        //Create a temporary file to analyse
        UUID fileDesignator = UUID.randomUUID();
        String testName = fileDesignator + MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.FILE_FORMAT);

        //copy temp file from testing data
        Path copied = Paths.get(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT) + testName);
        Path originalPath = new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_TESTING_FILE)).toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        AnalyseGenreRequest request = new AnalyseGenreRequest(fileDesignator);
        AnalyseGenreResponse response = intelligenceService.analyseGenre(request);
        assertTrue(response.getGenreArray().length > 0);

        //delete the temporary file
        assertTrue(new File(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MIDI_STORAGE_ROOT) + testName).delete());
    }


}
