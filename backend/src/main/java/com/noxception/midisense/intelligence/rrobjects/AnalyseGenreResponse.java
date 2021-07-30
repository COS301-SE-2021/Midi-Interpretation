package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.dataclass.KeySignature;

public class AnalyseGenreResponse extends ResponseObject {

    private final Genre genre;//have we decided if genre should be a string[]

    public AnalyseGenreResponse(Genre g) { this.genre = g;}

    public Genre getGenre() {
        return genre;
    }
}
