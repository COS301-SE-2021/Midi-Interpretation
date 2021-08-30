package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.intelligence.dataclass.ChordPrediction;
import com.noxception.midisense.intelligence.dataclass.GenrePredication;
import com.noxception.midisense.intelligence.exceptions.EmptyChordException;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.intelligence.strategies.ChordAnalysisStrategy;
import com.noxception.midisense.intelligence.strategies.GenreAnalysisStrategy;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final StandardConfig configurations;
    private GenreAnalysisStrategy genreAnalysisStrategy;
    private ChordAnalysisStrategy chordAnalysisStrategy;

    public IntelligenceServiceImpl(StandardConfig configurations) {
        this.configurations = configurations;
    }

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

        byte[] fileContents;

        //====================================
        try{
            String fileName =
                    configurations.configuration(ConfigurationName.MIDI_STORAGE_ROOT)
                    +fileDesignator
                    +configurations.configuration(ConfigurationName.FILE_FORMAT);

            Path path = Paths.get(fileName);
            fileContents = Files.readAllBytes(path);
        }
        catch (IOException e) {
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));
        }
        //====================================

        //check to see if there is a strategy
        if(this.genreAnalysisStrategy==null)
            throw new MissingStrategyException(configurations.configuration(ConfigurationName.MISSING_ANALYSIS_STRATEGY_EXCEPTION_TEXT));

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
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req) throws MissingStrategyException, EmptyChordException {
        ChordPrediction prediction = chordAnalysisStrategy.classify(req.getCompound());
        AnalyseChordResponse response = new AnalyseChordResponse(
                prediction.getRootNote(),
                prediction.getChordType().getShortName(),
                prediction.getBassNote()
        );
        return response;
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
    public void attachGenreStrategy(GenreAnalysisStrategy gs){
        this.genreAnalysisStrategy = gs;
    }

    public boolean hasGenreStrategy(){
        return this.genreAnalysisStrategy != null;
    }


    /**Method to attach a chord analysis strategy to the service
     *
     * @param cs a valid chord analysis strategy
     */
    public void attachChordStrategy(ChordAnalysisStrategy cs){
        this.chordAnalysisStrategy = cs;
    }

    public boolean hasChordStrategy(){
        return this.chordAnalysisStrategy != null;
    }
}
