package com.noxception.midisense.intelligence;

import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;

public interface IntelligenceService {

    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest req);

}