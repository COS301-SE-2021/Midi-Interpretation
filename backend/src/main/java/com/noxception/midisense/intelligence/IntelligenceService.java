package com.noxception.midisense.intelligence;

import com.noxception.midisense.intelligence.rrobjects.*;

public interface IntelligenceService {

    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req);
    public AnalyseChordResponse analyseChord(AnalyseChordRequest req);
    public AnalyseTonalityResponse analyseTonality(AnalyseTonalityRequest req);
}