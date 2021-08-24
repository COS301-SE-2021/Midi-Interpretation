package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.ChordPrediction;

/**
 * Provides an interface for a strategy used to analyse the chord of a track. Makes up the abstract strategy of the
 * Strategy Design Pattern used within the IntelligenceServiceContext.
 *
 */
public interface ChordAnalysisStrategy {

    ChordPrediction classify(byte[] features);
}
