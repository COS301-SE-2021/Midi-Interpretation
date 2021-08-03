package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.GenrePredication;

public class NeuralNetworkGenreAnalysisStrategy implements GenreAnalysisStrategy{
    @Override
    public GenrePredication[] classify(byte[] features) {
        return new GenrePredication[0];
    }
}
