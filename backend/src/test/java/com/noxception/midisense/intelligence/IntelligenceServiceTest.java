package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.intelligence.dataclass.ChordPrediction;
import com.noxception.midisense.intelligence.dataclass.ChordType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class IntelligenceServiceTest extends MIDISenseUnitTest {

    private IntelligenceServiceImpl intelligenceService;
    private StandardConfig configurations;

    @BeforeEach
    public void mountModule(){
        configurations = new MockConfigurationSettings();
        intelligenceService = new IntelligenceServiceImpl(configurations);
        intelligenceService.attachGenreStrategy(new NeuralNetworkGenreAnalysisStrategy(configurations));
        intelligenceService.attachChordStrategy(new DecisionTreeChordAnalysisStrategy());
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

    @Test
    public void testWhiteBox_AnalyseChord_IfOpenFifth_ThenAdheresToIntervals() throws MissingStrategyException {
        for(int k=0; k<12; k++){

            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k), (byte) (7+k)},
                    new byte[]{(byte) (60+k), (byte) (67+k)},
                    new byte[]{(byte) (0+k),(byte) (7+k),(byte) (12+k),(byte) (19+k)},
                    new byte[]{(byte) (7+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (12+k),(byte) (19+k)},
            };

            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.OPEN_FIFTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.OPEN_FIFTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.OPEN_FIFTH),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.OPEN_FIFTH),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.OPEN_FIFTH),
            };

            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();

                //System.out.printf("EXPECTED %s GOT %s%n",expectedChord,responseChord);

                assertEquals(responseChord,expectedChord);
            }



        }
    }



}
