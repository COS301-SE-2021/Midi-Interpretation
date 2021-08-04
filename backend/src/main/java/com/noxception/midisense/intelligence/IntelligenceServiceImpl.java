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

    /**Method that creates a list of genre classifications based on a byte stream of file features, according to a predefined
     * strategy
     *
     * @param req encapsulates a file designator for the midi song in question
     * @return an objects encapsulating a list of genres along with their certainty
     * @throws InvalidDesignatorException if the specified file does not exist
     * @throws MissingStrategyException if there is no predefined analysis strategy
     */
    @Override
    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req) throws InvalidDesignatorException, MissingStrategyException {

        //get the file corresponding to the designator
        UUID fileDesignator = req.getFileDesignator();

        //TODO: open the file, get contents as byte stream
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

    /**Method that creates a list of chord classifications based on a list of pitch data, according to a predefined
     * strategy
     *
     * @param req encapsulates an array of pitches encoded as bytes in agreement with the standard midi file format
     * @return an objects encapsulating a list of chord along with their certainty
     * @throws MissingStrategyException if there is no predefined analysis strategy
     */
    @Override
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req) throws MissingStrategyException{
        return null;
    }

    /**Method that creates a list of tonal (key) classifications based on a byte stream of file features, according to a predefined
     * strategy
     *
     * @param req encapsulates a file designator for the midi song in question
     * @return an objects encapsulating a list of tonalities along with their certainty
     * @throws InvalidDesignatorException if the specified file does not exist
     * @throws MissingStrategyException if there is no predefined analysis strategy
     */
    @Override
    public AnalyseTonalityResponse analyseTonality(AnalyseTonalityRequest req) throws InvalidDesignatorException, MissingStrategyException{
        return null;
    }


    /**Method to attach a genre analysis strategy to the service
     *
     * @param gs a valid genre analysis strategy
     */
    private void attachGenreStrategy(GenreAnalysisStrategy gs){
        this.genreAnalysisStrategy = gs;
    }
}
