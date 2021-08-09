package com.noxception.midisense.display;

import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;

/**
 * Class that is used for delivering structured responses to queries concerning:
 *
 * <ul>
 * <li>Composition Metadata </li>
 * <li>Midi Track Listing and Information</li>
 * <li>Midi Track Overview</li>
 * </ul>
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
public interface DisplayService {

    GetPieceMetadataResponse getPieceMetadata(GetPieceMetadataRequest request) throws InvalidDesignatorException;
    GetTrackInfoResponse getTrackInfo(GetTrackInfoRequest request) throws InvalidDesignatorException;
    GetTrackMetadataResponse getTrackMetadata(GetTrackMetadataRequest request) throws InvalidDesignatorException, InvalidTrackException;
    GetTrackOverviewResponse getTrackOverview(GetTrackOverviewRequest request) throws InvalidDesignatorException, InvalidTrackException;

}
