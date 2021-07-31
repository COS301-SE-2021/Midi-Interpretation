package com.noxception.midisense.intelligence.rrobjects;

import com.noxception.midisense.config.dataclass.ResponseObject;

public class AnalyseGenreResponse extends ResponseObject {

        private final String[] classification; //genre classification

        public AnalyseGenreResponse(String[] ss) {
                this.classification =ss;
        }

        public String[] getGenre() {
                return classification;
        }
}
