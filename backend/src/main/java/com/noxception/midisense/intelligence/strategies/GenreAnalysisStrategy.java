package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.GenrePredication;

public interface GenreAnalysisStrategy {

    public GenrePredication[] classify(byte[] features);
}
