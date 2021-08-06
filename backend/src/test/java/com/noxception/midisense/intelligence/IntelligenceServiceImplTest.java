package com.noxception.midisense.intelligence;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;
import com.noxception.midisense.intelligence.strategies.NeuralNetworkGenreAnalysisStrategy;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void test_AnalyseGenre_IfValidByteStream_ThenExpectPredictions() throws InvalidDesignatorException, MissingStrategyException {
        AnalyseGenreRequest request = new AnalyseGenreRequest(UUID.randomUUID());
        AnalyseGenreResponse response = intelligenceService.analyseGenre(request);
        assertTrue(response.getGenreArray().length > 0);
    }


}
