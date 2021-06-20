package com.noxception.midisense.display;

import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;

public interface DisplayService {
    GetPieceMetadataResponse getPieceMetadata(GetPieceMetadataRequest request) throws InvalidDesignatorException;
    GetTrackInfoResponse getTrackInfo(GetTrackInfoRequest request) throws InvalidDesignatorException;
    GetTrackMetadataResponse getTrackMetadata(GetTrackMetadataRequest request) throws InvalidDesignatorException, InvalidTrackException;
    GetTrackOverviewResponse getTrackOverview(GetTrackOverviewRequest request) throws InvalidDesignatorException, InvalidTrackException;
}
