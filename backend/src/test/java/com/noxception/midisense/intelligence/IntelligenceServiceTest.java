package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.dataclass.MockRepository;
import com.noxception.midisense.intelligence.dataclass.ChordPrediction;
import com.noxception.midisense.intelligence.dataclass.ChordType;
import com.noxception.midisense.intelligence.exceptions.EmptyChordException;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseChordRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseChordResponse;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;
import com.noxception.midisense.intelligence.strategies.DecisionTreeChordAnalysisStrategy;
import com.noxception.midisense.intelligence.strategies.NeuralNetworkGenreAnalysisStrategy;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.parser.Score;
import com.noxception.midisense.repository.DatabaseManager;
import com.noxception.midisense.repository.ScoreEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class IntelligenceServiceTest {

    private IntelligenceService intelligenceService;
    private StandardConfig configurations;
    private DatabaseManager databaseManager;

    @BeforeEach
    public void mountModule(){
        databaseManager = new MockRepository();
        configurations = new MockConfigurationSettings();
        intelligenceService = new IntelligenceServiceImpl(databaseManager,configurations);
        ((IntelligenceServiceImpl) intelligenceService).attachGenreStrategy(new NeuralNetworkGenreAnalysisStrategy(configurations));
        ((IntelligenceServiceImpl)intelligenceService).attachChordStrategy(new DecisionTreeChordAnalysisStrategy());
    }

    @Test
    public void test_AnalyseGenre_IfValidByteStream_ThenExpectPredictions() throws InvalidDesignatorException, MissingStrategyException, IOException {

        //Create a temporary entity to analyse
        UUID fileDesignator = UUID.randomUUID();

        //copy test data from file to an entity - save it to the 'DB'
        Path source = Paths.get(configurations.configuration(ConfigurationName.MIDI_TESTING_FILE));
        byte[] contents = Files.readAllBytes(source);
        ScoreEntity entity = new ScoreEntity(new Score(),fileDesignator,contents);
        databaseManager.save(entity);

        AnalyseGenreRequest request = new AnalyseGenreRequest(fileDesignator);
        AnalyseGenreResponse response = intelligenceService.analyseGenre(request);
        assertTrue(response.getGenreArray().length > 0);
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfOpenFifth_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
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


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfMajor_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (4+k), (byte) (7+k)},
                    new byte[]{(byte) (60+k),(byte) (64+k), (byte) (67+k)},
                    new byte[]{(byte) (0+k),(byte) (4+k),(byte) (7+k),(byte) (12+k),(byte) (16+k),(byte) (19+k)},
                    new byte[]{(byte) (4+k),(byte) (7+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (12+k),(byte) (16+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MAJOR),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MAJOR),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MAJOR),
                    new ChordPrediction((byte) (0+k),(byte) (4+k), ChordType.MAJOR),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.MAJOR),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfMinor_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (3+k), (byte) (7+k)},
                    new byte[]{(byte) (60+k),(byte) (63+k), (byte) (67+k)},
                    new byte[]{(byte) (0+k),(byte) (3+k),(byte) (7+k),(byte) (12+k),(byte) (15+k),(byte) (19+k)},
                    new byte[]{(byte) (3+k),(byte) (7+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (12+k),(byte) (15+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MINOR),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MINOR),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MINOR),
                    new ChordPrediction((byte) (0+k),(byte) (3+k), ChordType.MINOR),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.MINOR),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfDiminished_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (3+k), (byte) (6+k)},
                    new byte[]{(byte) (60+k),(byte) (63+k), (byte) (66+k)},
                    new byte[]{(byte) (0+k),(byte) (3+k),(byte) (6+k),(byte) (12+k),(byte) (15+k),(byte) (18+k)},
                    new byte[]{(byte) (3+k),(byte) (6+k), (byte) (12+k)},
                    new byte[]{(byte) (6+k),(byte) (12+k),(byte) (15+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DIMINISHED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DIMINISHED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DIMINISHED),
                    new ChordPrediction((byte) (0+k),(byte) (3+k), ChordType.DIMINISHED),
                    new ChordPrediction((byte) (0+k),(byte) (6+k), ChordType.DIMINISHED),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfAugmented_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (4+k), (byte) (8+k)},
                    new byte[]{(byte) (60+k),(byte) (64+k), (byte) (68+k)},
                    new byte[]{(byte) (0+k),(byte) (4+k),(byte) (8+k),(byte) (12+k),(byte) (16+k),(byte) (20+k)},
                    new byte[]{(byte) (4+k),(byte) (8+k), (byte) (12+k)},
                    new byte[]{(byte) (8+k),(byte) (12+k),(byte) (16+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.AUGMENTED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.AUGMENTED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.AUGMENTED),
                    new ChordPrediction((byte) (4+k),(byte) (4+k), ChordType.AUGMENTED),
                    new ChordPrediction((byte) (8+k),(byte) (8+k), ChordType.AUGMENTED),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfSus4_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (5+k), (byte) (7+k)},
                    new byte[]{(byte) (60+k),(byte) (65+k), (byte) (67+k)},
                    new byte[]{(byte) (0+k),(byte) (5+k),(byte) (7+k),(byte) (12+k),(byte) (17+k),(byte) (19+k)},
                    new byte[]{(byte) (5+k),(byte) (7+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (12+k),(byte) (17+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.SUSPENDED_FOURTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.SUSPENDED_FOURTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.SUSPENDED_FOURTH),
                    new ChordPrediction((byte) (0+k),(byte) (5+k), ChordType.SUSPENDED_FOURTH),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.SUSPENDED_FOURTH),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfDominant7th_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (4+k), (byte) (7+k), (byte) (10+k)},
                    new byte[]{(byte) (60+k),(byte) (64+k), (byte) (67+k), (byte) (70+k)},
                    new byte[]{(byte) (0+k),(byte) (4+k),(byte) (7+k),(byte) (10+k),(byte) (12+k), (byte) (16+k),(byte) (19+k), (byte) (22+k)},
                    new byte[]{(byte) (4+k),(byte) (7+k), (byte) (10+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (10+k),(byte) (12+k), (byte) (16+k)},
                    new byte[]{(byte) (10+k),(byte) (12+k),(byte) (16+k), (byte) (19+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DOMINANT_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DOMINANT_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DOMINANT_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (4+k), ChordType.DOMINANT_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.DOMINANT_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (10+k), ChordType.DOMINANT_SEVENTH),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfMajor7th_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (4+k), (byte) (7+k), (byte) (11+k)},
                    new byte[]{(byte) (60+k),(byte) (64+k), (byte) (67+k), (byte) (71+k)},
                    new byte[]{(byte) (0+k),(byte) (4+k),(byte) (7+k),(byte) (11+k),(byte) (12+k), (byte) (16+k),(byte) (19+k), (byte) (23+k)},
                    new byte[]{(byte) (4+k),(byte) (7+k), (byte) (11+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (11+k),(byte) (12+k), (byte) (16+k)},
                    new byte[]{(byte) (11+k),(byte) (12+k),(byte) (16+k), (byte) (19+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MAJOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MAJOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MAJOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (4+k), ChordType.MAJOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.MAJOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (11+k), ChordType.MAJOR_SEVENTH),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfMinor7th_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (3+k), (byte) (7+k), (byte) (10+k)},
                    new byte[]{(byte) (60+k),(byte) (63+k), (byte) (67+k), (byte) (70+k)},
                    new byte[]{(byte) (0+k),(byte) (3+k),(byte) (7+k),(byte) (10+k),(byte) (12+k), (byte) (15+k),(byte) (19+k), (byte) (22+k)},
                    new byte[]{(byte) (3+k),(byte) (7+k), (byte) (10+k), (byte) (12+k)},
                    new byte[]{(byte) (7+k),(byte) (10+k),(byte) (12+k), (byte) (15+k)},
                    new byte[]{(byte) (10+k),(byte) (12+k),(byte) (15+k), (byte) (19+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MINOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MINOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.MINOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (3+k), ChordType.MINOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (7+k), ChordType.MINOR_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (10+k), ChordType.MINOR_SEVENTH),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfDiminished7th_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    new byte[]{(byte) (0+k),(byte) (3+k), (byte) (6+k), (byte) (9+k)},
                    new byte[]{(byte) (60+k),(byte) (63+k), (byte) (66+k), (byte) (69+k)},
                    new byte[]{(byte) (0+k),(byte) (3+k),(byte) (6+k),(byte) (9+k),(byte) (12+k), (byte) (15+k),(byte) (18+k), (byte) (21+k)},
                    new byte[]{(byte) (3+k),(byte) (6+k), (byte) (9+k), (byte) (12+k)},
                    new byte[]{(byte) (6+k),(byte) (9+k),(byte) (12+k), (byte) (15+k)},
                    new byte[]{(byte) (9+k),(byte) (12+k),(byte) (15+k), (byte) (18+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DIMINISHED_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DIMINISHED_SEVENTH),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.DIMINISHED_SEVENTH),
                    new ChordPrediction((byte) (3+k),(byte) (3+k), ChordType.DIMINISHED_SEVENTH),
                    new ChordPrediction((byte) (6+k),(byte) (6+k), ChordType.DIMINISHED_SEVENTH),
                    new ChordPrediction((byte) (9+k),(byte) (9+k), ChordType.DIMINISHED_SEVENTH),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    public void testWhiteBox_AnalyseChord_IfAltered_ThenAdheresToIntervals() throws MissingStrategyException, EmptyChordException {

        //For each pitch offset
        for(int k=0; k<12; k++){


            byte[][] testingCases = new byte[][]{
                    //these need to change a bit
                    new byte[]{(byte) (0+k),(byte) (2+k), (byte) (3+k), (byte) (4+k)},
                    new byte[]{(byte) (60+k),(byte) (85+k), (byte) (61+k), (byte) (62+k)},
                    new byte[]{(byte) (0+k),(byte) (3+k),(byte) (17+k),(byte) (4+k),(byte) (5+k), (byte) (15+k),(byte) (18+k), (byte) (21+k)},
                    new byte[]{(byte) (0+k),(byte) (6+k), (byte) (17+k), (byte) (12+k)},
                    new byte[]{(byte) (0+k),(byte) (1+k),(byte) (12+k), (byte) (16+k)},
                    new byte[]{(byte) (0+k),(byte) (17+k),(byte) (18+k), (byte) (18+k)},
            };


            ChordPrediction[] testingResponses = new ChordPrediction[]{
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.ALTERED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.ALTERED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.ALTERED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.ALTERED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.ALTERED),
                    new ChordPrediction((byte) (0+k),(byte) (0+k), ChordType.ALTERED),
            };


            //Testing all cases with their expected responses
            for(int j=0; j<testingCases.length; j++){

                AnalyseChordRequest request = new AnalyseChordRequest(testingCases[j]);
                AnalyseChordResponse response = intelligenceService.analyseChord(request);

                String responseChord = response.getChord();
                String expectedChord = testingResponses[j].getCommonName();
                assertEquals(responseChord,expectedChord);
            }

        }
    }

    @Test
    @DisplayName("Analyse Chord: input [Valid Byte Stream] expect [chord analysis]")
    public void testBlackBox_AnalyseChord_IfValidByteStream() throws IllegalArgumentException, MissingStrategyException, EmptyChordException {

        //valid byte stream
        byte[] validStream = new byte[]{3, 6, 9, 12};

        //test with request response
        AnalyseChordRequest request = new AnalyseChordRequest(validStream);
        AnalyseChordResponse response = intelligenceService.analyseChord(request);

        //Expect chord analysis
        Assertions.assertNotNull(response.getChord());

    }

    @Test
    @DisplayName("Analyse Chord: input [Invalid Byte Stream] expect [Empty chord Exception]")
    public void testBlackBox_AnalyseChord_IfEmptyByteStream_ThrowException() throws IllegalArgumentException{

        //invalid byte stream
        byte[] validStream = new byte[]{};

        //test with request response
        AnalyseChordRequest request = new AnalyseChordRequest(validStream);

        //check that exception was thrown
        Assertions.assertThrows(
                EmptyChordException.class,//for a request
                ()->intelligenceService.analyseChord(request));//because

    }


}
