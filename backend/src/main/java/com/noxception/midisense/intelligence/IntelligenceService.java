package com.noxception.midisense.intelligence;

import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.springframework.stereotype.Service;

@Service
public interface IntelligenceService {

    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req) throws InvalidDesignatorException;
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req);
    public AnalyseTonalityResponse analyseTonality(AnalyseTonalityRequest req);

}