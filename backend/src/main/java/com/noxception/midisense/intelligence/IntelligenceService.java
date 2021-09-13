package com.noxception.midisense.intelligence;

import com.noxception.midisense.intelligence.exceptions.EmptyChordException;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.springframework.stereotype.Service;

@Service
public interface IntelligenceService {

    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req) throws InvalidDesignatorException, MissingStrategyException;
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req) throws MissingStrategyException, EmptyChordException;
    public AnalyseTonalityResponse analyseTonality(AnalyseTonalityRequest req) throws InvalidDesignatorException, MissingStrategyException;

}