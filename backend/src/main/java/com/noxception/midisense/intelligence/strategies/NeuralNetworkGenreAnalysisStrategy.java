package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.GenrePredication;

public class NeuralNetworkGenreAnalysisStrategy implements GenreAnalysisStrategy{
    /**
     * Method to classify the most likely genres to be associated with a piece
     * @param features placeholder
     * @return an array of predicted genres for the piece
     */
    @Override
    public GenrePredication[] classify(byte[] features) {
        return new GenrePredication[0];
    }
}
