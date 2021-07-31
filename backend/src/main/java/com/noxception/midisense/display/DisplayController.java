package com.noxception.midisense.display;

import com.noxception.midisense.api.DisplayApi;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin(value = MIDISenseConfig.CROSS_ORIGIN)
@RestController
public class DisplayController implements DisplayApi {

    @Autowired
    DisplayServiceImpl displayService;

    @Override
    public ResponseEntity<DisplayGetPieceMetadataResponse> getPieceMetadata(DisplayGetPieceMetadataRequest body) {
        DisplayGetPieceMetadataResponse responseObject = new DisplayGetPieceMetadataResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            //========================
            GetPieceMetadataRequest req = new GetPieceMetadataRequest(fileDesignator);
            GetPieceMetadataResponse res = displayService.getPieceMetadata(req);
            //========================
            responseObject.setKeySignature(res.getKeySignature().toString());
            responseObject.setTempoIndication(res.getTempoIndication().getTempo());
            //-------
            DisplayGetPieceMetadataResponseTimeSignature timeSignature = new DisplayGetPieceMetadataResponseTimeSignature();
            timeSignature.setBeatValue(res.getTimeSignature().getBeatValue());
            timeSignature.setNumBeats(res.getTimeSignature().getNumBeats());
            responseObject.setTimeSignature(timeSignature);
            //-------
        }
        catch(InvalidDesignatorException | IllegalArgumentException e){
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    @Override
    public ResponseEntity<DisplayGetTrackInfoResponse> getTrackInfo(DisplayGetTrackInfoRequest body) {
        DisplayGetTrackInfoResponse responseObject = new DisplayGetTrackInfoResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            //========================
            GetTrackInfoRequest req = new GetTrackInfoRequest(fileDesignator);
            GetTrackInfoResponse res = displayService.getTrackInfo(req);
            //========================
            for(byte index: res.getTrackIndices()){
                String trackName = res.getTrackMap().get(index);
                DisplayGetTrackInfoResponseInner inner = new DisplayGetTrackInfoResponseInner();
                inner.setIndex((int) index);
                inner.setTrackName(trackName);
                responseObject.add(inner);
            }
        }
        catch(InvalidDesignatorException | IllegalArgumentException e){
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    @Override
    public ResponseEntity<DisplayGetTrackMetadataResponse> getTrackMetadata(DisplayGetTrackMetadataRequest body) {
        DisplayGetTrackMetadataResponse responseObject = new DisplayGetTrackMetadataResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            int trackIndex = body.getTrackIndex();
            //========================
            GetTrackMetadataRequest req = new GetTrackMetadataRequest(fileDesignator,(byte) trackIndex);
            GetTrackMetadataResponse res = displayService.getTrackMetadata(req);
            //========================
            responseObject.setTrackString(res.getTrackString());
        }
        catch(InvalidDesignatorException | IllegalArgumentException | InvalidTrackException e){
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    @Override
    public ResponseEntity<DisplayGetTrackOverviewResponse> getTrackOverview(DisplayGetTrackOverviewRequest body) {
        DisplayGetTrackOverviewResponse responseObject = new DisplayGetTrackOverviewResponse();
        HttpStatus returnStatus = HttpStatus.OK;
        try{
            UUID fileDesignator = UUID.fromString(body.getFileDesignator());
            int trackIndex = body.getTrackIndex();
            //========================
            GetTrackOverviewRequest req = new GetTrackOverviewRequest(fileDesignator,(byte) trackIndex);
            GetTrackOverviewResponse res = displayService.getTrackOverview(req);
            //========================
            responseObject.addAll(res.getPitchArray());
        }
        catch(InvalidDesignatorException | IllegalArgumentException | InvalidTrackException e){
            returnStatus = HttpStatus.BAD_REQUEST;
            responseObject = null;
        }
        return new ResponseEntity<>(responseObject,returnStatus);
    }

    //TODO: WORK ON CONTROLLER
}
