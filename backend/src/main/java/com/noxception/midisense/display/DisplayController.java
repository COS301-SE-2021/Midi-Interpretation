package com.noxception.midisense.display;

import com.noxception.midisense.api.DisplayApi;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = MIDISenseConfig.CROSS_ORIGIN)
@RestController
public class DisplayController implements DisplayApi {

    @Override
    public ResponseEntity<DisplayGetPieceMetadataResponse> getPieceMetadata(DisplayGetPieceMetadataRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<DisplayGetTrackInfoResponse> getTrackInfo(DisplayGetTrackInfoRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<DisplayGetTrackMetadataResponse> getTrackMetadata(DisplayGetTrackMetadataRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<DisplayGetTrackOverviewResponse> getTrackOverview(DisplayGetTrackOverviewRequest body) {
        return null;
    }

    //TODO: WORK ON CONTROLLER
}
