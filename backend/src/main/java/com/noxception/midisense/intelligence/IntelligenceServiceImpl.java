package com.noxception.midisense.intelligence;

import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import org.springframework.stereotype.Service;


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
    @Override
    public AnalyseGenreResponse analyseGenre(AnalyseGenreRequest request) throws InvalidDesignatorException, InvalidTrackException {
        return null;
    }
}
