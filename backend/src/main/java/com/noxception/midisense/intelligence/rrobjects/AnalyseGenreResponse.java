package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;
import java.util.List;

public class AnalyseGenreResponse extends ResponseObject {
    /**
     * ATTRIBUTE
     */
    private final String[] genreArray;

    /**
     * CONSTRUCTOR
     * @param genreArray an array consisting of the most prevelant genres in the piece
     */
    public AnalyseGenreResponse(String[] genreArray) {
        this.genreArray = genreArray;
    }

    /**
     * GET method
     * @return genreArray
     */
    public String[] getGenreArray() {
        return genreArray;
    }
}
