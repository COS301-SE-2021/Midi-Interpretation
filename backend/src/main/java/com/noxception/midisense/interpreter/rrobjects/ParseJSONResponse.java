package com.noxception.midisense.interpreter.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;
import com.noxception.midisense.interpreter.parser.Score;

public class ParseJSONResponse extends ResponseObject {

    /** ATTRIBUTE */
    private final Score parsedScore; //Json score response object

    /**
     * CONSTRUCTOR
     * @param parsedScore response score as JSON object
     */
    public ParseJSONResponse(Score parsedScore) {
        this.parsedScore = parsedScore;
    }

    /** GET Method */
    public Score getParsedScore() {
        return parsedScore;
    }
}
