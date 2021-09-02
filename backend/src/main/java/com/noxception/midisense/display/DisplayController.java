package com.noxception.midisense.display;

import com.noxception.midisense.api.DisplayApi;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.parser.KeySignature;
import com.noxception.midisense.interpreter.parser.TempoIndication;
import com.noxception.midisense.interpreter.parser.TimeSignature;
import com.noxception.midisense.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Class that invokes Display service methods by interpreting requests made to endpoints outlined
 * by the DisplayAPI interface, a service layer framework generated by Swagger 2.0.
 *
 * This class handles mapping between service layer request and response bodies, and business layer service request
 * and response objects. Any errors that are encountered by the service during method calls are interpreted
 * and dealt with here.
 *
 * For a detailed description of the controller interface, see {@link DisplayApi} for the definition
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
public class DisplayController implements DisplayApi {


    private final DisplayServiceImpl displayService;

    @Autowired
    public DisplayController(DisplayServiceImpl displayService) {
        this.displayService = displayService;
    }

    /** Method that invokes the getPieceMetadata method of the Display service and presents the resultant metadata of
     * the work with a specific designator.
     *
     * @param body the request body as interpreted by the service layer framework. See {@link DisplayApi}
     * @return a tuple of the appropriate http status code and response object:
     * Possible valid tuples are a 200 status code and an object that is not null corresponding to
     * a successful request, or a 400 status code and a null object corresponding to a malformed request field.
     */
    @Override
    public ResponseEntity<DisplayGetPieceMetadataResponse> getPieceMetadata(DisplayGetPieceMetadataRequest body) {

        DisplayGetPieceMetadataResponse responseObject = new DisplayGetPieceMetadataResponse();
        HttpStatus returnStatus = HttpStatus.OK;

        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            GetPieceMetadataRequest req = new GetPieceMetadataRequest(fileDesignator);

            //Log the call for request
            log.info(String.format("Request | To: %s | For: %s | Assigned: %s","getPieceMetadata",fileDesignator,req.getDesignator()));

            GetPieceMetadataResponse res = displayService.getPieceMetadata(req);

            //set the tempo map items
            for(TempoIndication tempo : res.getTempoIndication()){
                DisplayGetPieceMetadataResponseTempoIndicationMap t = new DisplayGetPieceMetadataResponseTempoIndicationMap();
                t.setTick(tempo.tick);
                t.setTempoIndication(BigDecimal.valueOf(tempo.tempoIndication));
                responseObject.addTempoIndicationMapItem(t);
            }

            //set the key signature map items
            for(KeySignature key : res.getKeySignature()){
                DisplayGetPieceMetadataResponseKeySignatureMap k = new DisplayGetPieceMetadataResponseKeySignatureMap();
                k.setTick(key.tick);
                k.setKeySignature(key.commonName);
                responseObject.addKeySignatureMapItem(k);
            }

            //set the time signature map items
            for(TimeSignature time : res.getTimeSignature()){
                DisplayGetPieceMetadataResponseTimeSignatureMap t = new DisplayGetPieceMetadataResponseTimeSignatureMap();
                t.setTick(time.tick);

                DisplayGetPieceMetadataResponseTimeSignature inner = new DisplayGetPieceMetadataResponseTimeSignature();
                inner.setNumBeats(time.time.numBeats);
                inner.setBeatValue(time.time.beatValue);
                t.setTimeSignature(inner);
                responseObject.addTimeSignatureMapItem(t);
            }

            responseObject.setSuccess(true);
            responseObject.setMessage("Successfully retrieved metadata for "+fileDesignator);

        }
        catch(InvalidDesignatorException | IllegalArgumentException e){

            //Log the error
            log.warn(String.format("FAILURE | To: %s | Because: %s ","getPieceMetadata",e.getMessage()));

            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject.setSuccess(true);
            responseObject.setMessage(e.getMessage());

        }

        return new ResponseEntity<>(responseObject,returnStatus);
    }

    /** Method that invokes the getTrackInfo method of the Display service and presents the resultant track list of
     * the work with a specific designator.
     *
     * @param body the request body as interpreted by the service layer framework. See {@link DisplayApi}
     * @return a tuple of the appropriate http status code and response object:
     * Possible valid tuples are a 200 status code and an object that is not null corresponding to
     * a successful request, or a 400 status code and a null object corresponding to a malformed request field.
     */
    @Override
    public ResponseEntity<DisplayGetTrackInfoResponse> getTrackInfo(DisplayGetTrackInfoRequest body) {

        DisplayGetTrackInfoResponse responseObject = new DisplayGetTrackInfoResponse();
        HttpStatus returnStatus = HttpStatus.OK;

        try{

            UUID fileDesignator = UUID.fromString(body.getFileDesignator());

            GetTrackInfoRequest req = new GetTrackInfoRequest(fileDesignator);

            //Log the call for request
            log.info(String.format("Request | To: %s | For: %s | Assigned: %s","getTrackInfo",fileDesignator,req.getDesignator()));

            GetTrackInfoResponse res = displayService.getTrackInfo(req);

            for(byte index: res.getTrackIndices()){

                String trackName = res.getTrackMap().get(index);
                DisplayGetTrackInfoResponseInner inner = new DisplayGetTrackInfoResponseInner();

                inner.setIndex((int) index);
                inner.setTrackName(trackName);

                responseObject.add(inner);
                inner.setSuccess(true);
                inner.setMessage("Successfully retrieved track");
            }


        }
        catch(InvalidDesignatorException | IllegalArgumentException e){

            //Log the error
            log.warn(String.format("FAILURE | To: %s | Because: %s ","getTrackInfo",e.getMessage()));

            returnStatus = HttpStatus.BAD_REQUEST;

        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    /** Method that invokes the getTrackMetadata method of the Display service and presents the resultant note metadata of
     * the track with a specific index within a file with set designator.
     *
     * @param body the request body as interpreted by the service layer framework. See {@link DisplayApi}
     * @return a tuple of the appropriate http status code and response object:
     * Possible valid tuples are a 200 status code and an object that is not null corresponding to
     * a successful request, or a 400 status code and a null object corresponding to a malformed request field.
     */
    @Override
    public ResponseEntity<DisplayGetTrackMetadataResponse> getTrackMetadata(DisplayGetTrackMetadataRequest body) {

        DisplayGetTrackMetadataResponse responseObject = new DisplayGetTrackMetadataResponse();
        HttpStatus returnStatus = HttpStatus.OK;

        try{

            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            int trackIndex = body.getTrackIndex();

            GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,(byte) trackIndex);

            //Log the call for request
            log.info(String.format("Request | To: %s in channel %s| For: %s | Assigned: %s","getTrackMetadata",trackIndex,fileDesignator,req.getDesignator()));

            GetTrackMetadataResponse res = displayService.getTrackMetadata(req);

            responseObject.setTrackString(res.getTrackString());
            responseObject.setSuccess(true);
            responseObject.setMessage(String.format("Successfully retrieved track [%s:%s]",trackIndex,fileDesignator));

        }
        catch(InvalidDesignatorException | IllegalArgumentException | InvalidTrackException e){

            //Log the error
            log.warn(String.format("FAILURE | To: %s | Because: %s ","getTrackMetadata",e.getMessage()));

            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject.setSuccess(false);
            responseObject.setMessage(e.getMessage());

        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    /** Method that invokes the getTrackOverview method of the Display service and presents the resultant summary of
     * the resultant note metadata of the track with a specific index within a file corresponding to the set designator.
     *
     * @param body the request body as interpreted by the service layer framework. See {@link DisplayApi}
     * @return a tuple of the appropriate http status code and response object:
     * Possible valid tuples are a 200 status code and an object that is not null corresponding to
     * a successful request, or a 400 status code and a null object corresponding to a malformed request field.
     */
    @Override
    public ResponseEntity<DisplayGetTrackOverviewResponse> getTrackOverview(DisplayGetTrackOverviewRequest body) {

        DisplayGetTrackOverviewResponse responseObject = new DisplayGetTrackOverviewResponse();
        HttpStatus returnStatus = HttpStatus.OK;

        try{

            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            int trackIndex = body.getTrackIndex();

            GetTrackOverviewRequest req = new GetTrackOverviewRequest(fileDesignator,(byte) trackIndex);

            //Log the call for request
            log.info(String.format("Request | To: %s in channel %s| For: %s | Assigned: %s","getTrackOverview",trackIndex,fileDesignator,req.getDesignator()));

            GetTrackOverviewResponse res = displayService.getTrackOverview(req);

            responseObject.setTrackArray(res.getPitchArray());
            responseObject.setSuccess(true);
            responseObject.setMessage(String.format("Successfully retrieved track [%s:%s]",trackIndex,fileDesignator));

        }
        catch(InvalidDesignatorException | IllegalArgumentException | InvalidTrackException e){

            //Log the error
            log.warn(String.format("FAILURE | To: %s | Because: %s ","getTrackOverview",e.getMessage()));

            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject.setSuccess(false);
            responseObject.setMessage(e.getMessage());

        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

}
