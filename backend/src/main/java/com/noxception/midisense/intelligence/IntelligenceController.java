package com.noxception.midisense.intelligence;

import com.noxception.midisense.api.IntelligenceApi;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.intelligence.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@CrossOrigin("*")
@RestController
public class IntelligenceController implements IntelligenceApi {

    @Autowired
    IntelligenceServiceImpl intelligenceService;


    @Override
    public ResponseEntity<IntelligenceAnalyseGenreResponse> intelligenceAnalyseGenrePost(IntelligenceAnalyseGenreRequest body) {
        IntelligenceAnalyseGenreResponse responseObject = new IntelligenceAnalyseGenreResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            //========================
            AnalyseGenreRequest req = new AnalyseGenreRequest(fileDesignator);
            AnalyseGenreResponse res = intelligenceService.analyseGenre(req);
            //=======================
            //responseObject.addAll(res.getGenreArray());
            for(String genre: res.getGenreArray()){
                responseObject.add(genre);
            }
        }
        catch(InvalidDesignatorException | IllegalArgumentException e){
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }
}
