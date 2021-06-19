package com.noxception.midisense.display;

import com.noxception.midisense.display.rrobjects.*;

public interface DisplayService {
    GetPieceMetadataResponse getPieceMetadata(GetPieceMetadataRequest request);
    GetTrackInfoResponse getTrackInfo(GetTrackInfoRequest request);
    GetTrackMetadataResponse getPieceMetadata(GetTrackMetadataRequest request);
    GetTrackOverviewResponse getPieceOverview(GetTrackOverviewRequest request);
}
