package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.intelligence.dataclass.GenrePredication;

import java.util.ArrayList;
import java.util.List;

public class AnalyseGenreResponse extends ResponseObject {
    /**
     * ATTRIBUTE
     */
    private final GenrePredication[] genreArray;

    /**
     * CONSTRUCTOR
     * @param genreArray an array consisting of the most prevelant genres in the piece
     */
    public AnalyseGenreResponse(GenrePredication[] genreArray) {
        this.genreArray = genreArray;
    }

    /**
     * GET method
     * @return genreArray
     */
    public GenrePredication[] getGenreArray() {
        return genreArray;
    }
}
