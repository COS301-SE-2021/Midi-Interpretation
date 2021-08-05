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
        // TODO: replace mock with working classifier
        return new GenrePredication[]{
                new GenrePredication("Rock",0.98),
                new GenrePredication("Pop",0.92),
                new GenrePredication("Ska",0.56)
        };
    }
}
