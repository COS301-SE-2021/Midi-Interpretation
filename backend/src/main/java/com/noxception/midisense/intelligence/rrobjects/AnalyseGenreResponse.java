package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

import java.util.ArrayList;
import java.util.List;

public class AnalyseGenreResponse extends ResponseObject {
    /**
     * ATTRIBUTE
     */
    private final List<String> genreArray;

    /**
     * CONSTRUCTOR
     * @param genreArray an array consisting of the most prevelant genres in the piece
     */
    public AnalyseGenreResponse(List<String> genreArray) {
        this.genreArray = genreArray;
    }

    /**
     * GET method
     * @return genreArray
     */
    public List<String> getGenreArray() {
        return genreArray;
    }
}
