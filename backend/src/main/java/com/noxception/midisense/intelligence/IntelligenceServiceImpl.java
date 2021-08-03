package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.intelligence.dataclass.GenrePredication;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.intelligence.strategies.GenreAnalysisStrategy;
import com.noxception.midisense.intelligence.strategies.NeuralNetworkGenreAnalysisStrategy;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Class that is used for the analysis of compositional elements. The primary function is that of genre analysis, whereby a collection of genres is returned for a specific file, within a degree of confidence
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
@Service
public class IntelligenceServiceImpl implements IntelligenceService{

    private GenreAnalysisStrategy genreAnalysisStrategy;

    @Override
    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req) throws InvalidDesignatorException, MissingStrategyException {

        //get the file corresponding to the designator
        UUID fileDesignator = req.getFileDesignator();

        //open the file, get contents as byte stream
        //====================================
        byte[] fileContents = new byte[]{0,1};
        //====================================

        //check to see if there is a strategy
        if(this.genreAnalysisStrategy==null)
            throw new MissingStrategyException(MIDISenseConfig.configuration(MIDISenseConfig.ConfigurationName.MISSING_ANALYSIS_STRATEGY_EXCEPTION_TEXT));

        //map the file features
        GenrePredication[] classifications = genreAnalysisStrategy.classify(fileContents);

        //map the features to the genre classification object
        return new AnalyseGenreResponse(classifications);
    }

    @Override
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req) {
        return null;
    }

    @Override
    public AnalyseTonalityResponse analyseTonality(AnalyseTonalityRequest req) {
        return null;
    }

    private void attachGenreStrategy(GenreAnalysisStrategy gs){
        this.genreAnalysisStrategy = gs;
    }
}
