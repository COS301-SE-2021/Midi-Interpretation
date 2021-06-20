package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.parser.Score;

public class ParseJSONResponse extends ResponseObject {
    private final Score parsedScore;

    public ParseJSONResponse(Score parsedScore) {
        this.parsedScore = parsedScore;
    }

    public Score getParsedScore() {
        return parsedScore;
    }
}
