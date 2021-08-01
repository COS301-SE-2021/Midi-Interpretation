package com.noxception.midisense.intelligence;

import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;

public interface IntelligenceService {

    AnalyseGenreResponse analyseGenre(AnalyseGenreRequest request) throws InvalidDesignatorException, InvalidTrackException;
}
