package com.noxception.midisense.intelligence;

import com.noxception.midisense.api.IntelligenceApi;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.intelligence.dataclass.GenrePredication;
import com.noxception.midisense.intelligence.exceptions.MissingStrategyException;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreRequest;
import com.noxception.midisense.intelligence.rrobjects.AnalyseGenreResponse;
import com.noxception.midisense.intelligence.strategies.NeuralNetworkGenreAnalysisStrategy;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.models.IntelligenceAnalyseGenreRequest;
import com.noxception.midisense.models.IntelligenceAnalyseGenreResponse;
import com.noxception.midisense.models.IntelligenceAnalyseGenreResponseGenreArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class that invokes intelligence service methods by interpreting requests made to endpoints outlined
 * by the IntelligenceAPI interface, a service layer framework generated by Swagger 2.0.
 *
 * This class handles mapping between service layer request and response bodies, and business layer service request
 * and response objects. Any errors that are encountered by the service during method calls are interpreted
 * and dealt with here.
 *
 * For a detailed description of the controller interface, see {@link IntelligenceApi} for the definition
 * generated from the openAPI specification application.yaml.
 *
 * For a detailed description of the controller endpoints, please visit http://host:port/swagger-ui.html#/
 * when the Spring application is running.
 *
 *  * @author Adrian Rae
 *  * @author Claudio Teixeira
 *  * @author Hendro Smit
 *  * @author Mbuso Shakoane
 *  * @author Rearabetswe Maeko
 *  * @since 1.0.0
 */

@Slf4j
@CrossOrigin("*")
@RestController
@DependsOn({"configurationLoader"})
public class IntelligenceController implements IntelligenceApi {


    private final IntelligenceServiceImpl intelligenceService;

    @Autowired
    public IntelligenceController(IntelligenceServiceImpl intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    @Override
    public ResponseEntity<IntelligenceAnalyseGenreResponse> analyseGenre(IntelligenceAnalyseGenreRequest body) {

        IntelligenceAnalyseGenreResponse responseObject = new IntelligenceAnalyseGenreResponse();
        HttpStatus returnStatus = HttpStatus.OK;

        try{

            UUID fileDesignator = UUID.fromString(body.getFileDesignator());

            AnalyseGenreRequest req = new AnalyseGenreRequest(fileDesignator);

            //Log the call for request
            log.info(String.format("Request | To: %s | For: %s | Assigned: %s","analyseGenre",fileDesignator,req.getDesignator()));

            if(!intelligenceService.hasGenreStrategy())
                intelligenceService.attachGenreStrategy(new NeuralNetworkGenreAnalysisStrategy(new MIDISenseConfig()));
            AnalyseGenreResponse res = intelligenceService.analyseGenre(req);


            List<IntelligenceAnalyseGenreResponseGenreArray> list = new ArrayList<>();
            for(GenrePredication genre: res.getGenreArray()){
                IntelligenceAnalyseGenreResponseGenreArray inner = new IntelligenceAnalyseGenreResponseGenreArray();
                inner.setName(genre.getGenreName());
                inner.setCertainty(BigDecimal.valueOf(genre.getCertainty()));
                list.add(inner);

            }
            responseObject.setGenreArray(list);

            responseObject.setSuccess(true);
            responseObject.setMessage("Successfully analysed file");

        }
        catch(InvalidDesignatorException | IllegalArgumentException | MissingStrategyException e){

            //Log the error
            log.warn(String.format("FAILURE | To: %s | Because: %s ","analyseGenre",e.getMessage()));

            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject.setSuccess(false);
            responseObject.setMessage(e.getMessage());

        }

        return new ResponseEntity<>(responseObject,returnStatus);
    }
}
