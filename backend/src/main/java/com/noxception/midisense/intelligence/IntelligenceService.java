package com.noxception.midisense.intelligence;

import com.noxception.midisense.intelligence.exceptions.EmptyChordException;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.springframework.stereotype.Service;
/**
 * Class that is used for delivering structured responses to queries concerning:
 *
 * <ul>
 * <li>Genre Analysis </li>
 * <li>Chord Analysis</li>
 * <li>Tonality Analysis</li>
 * </ul>
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
@Service
public interface IntelligenceService {

    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req) throws InvalidDesignatorException, MissingStrategyException;
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req) throws MissingStrategyException, EmptyChordException;
    public AnalyseTonalityResponse analyseTonality(AnalyseTonalityRequest req) throws InvalidDesignatorException, MissingStrategyException;

}