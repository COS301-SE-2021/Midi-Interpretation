package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.ChordPrediction;
import com.noxception.midisense.intelligence.dataclass.ChordType;

import java.util.*;

public class DecisionTreeChordAnalysisStrategy implements ChordAnalysisStrategy{

    public DecisionTreeChordAnalysisStrategy() {

    }

    @Override
    public ChordPrediction classify(byte[] features) {

        //see if there are notes in chord
        //TODO: Empty Chord Exception

        //pre-sort the pitches in ascending order
        Arrays.sort(features);

        //get the lowest pitch
        byte lowestPitch = features[0];

        //reduce the features to a set modulo 12
        Set<Byte> chordRegression = new HashSet<>();
        for(byte b: features){
            chordRegression.add((byte) (b % 12));
        }

        //see if there is a direct overlap of shape
        boolean foundShape = false;

        //get a map of all possible chord masks
        for(ChordType chord : ChordType.values()){
            Set<Byte> chordMask = chord.getByteMask();

            //for each possible rooting of the chord
            for(byte offset : chordRegression){
                //create a temporary copy of the chordMask offset by the rooting
                Set<Byte> temporaryShape = new HashSet<>();
                for(byte tone: chordMask){
                    temporaryShape.add((byte) ((tone + offset) % 12));
                }

                //see if the mask with the offset is the same as the chord:
                //IE, see if the both are subsets of one another
                boolean tempSubsetOfChord = chordRegression.containsAll(temporaryShape);
                boolean chordSubsetOfTemp = temporaryShape.containsAll(chordRegression);

                if(tempSubsetOfChord && chordSubsetOfTemp){
                    byte bassNote = (byte) (lowestPitch % 12);
                    return new ChordPrediction(offset,bassNote,chord);
                }



            }

        }

        //if no other chord matches, assume altered on the lowest pitch
        return new ChordPrediction((byte) (lowestPitch % 12),ChordType.ALTERED);
    }





}

