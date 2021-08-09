package com.noxception.midisense.display;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.repository.DatabaseManager;
import com.noxception.midisense.interpreter.repository.ScoreEntity;
import com.noxception.midisense.interpreter.repository.ScoreRepository;
import com.noxception.midisense.interpreter.repository.TrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
@Service
public class DisplayServiceImpl implements DisplayService{


    private final DatabaseManager databaseManager;
    private final StandardConfig configurations;

    @Autowired
    public DisplayServiceImpl(ScoreRepository scoreRepository, MIDISenseConfig midiSenseConfig) {
        databaseManager = new DatabaseManager();
        databaseManager.attachRepository(scoreRepository);
        configurations = midiSenseConfig;
    }

    public DisplayServiceImpl(DatabaseManager databaseManager, StandardConfig configurations) {
        this.databaseManager = databaseManager;
        this.configurations = configurations;
    }



    /**Used to retrieve the Metadata of an existing interpreted piece
     * @param request encapsulates a request with the designator of the work
     * @return an object encapsulating the key signature, time signature and tempo indication of the work
     * @throws InvalidDesignatorException if the designator does not correspond to an existing file
     */
    @Transactional
    @Override
    public GetPieceMetadataResponse getPieceMetadata(GetPieceMetadataRequest request) throws InvalidDesignatorException {

        if(request==null)
            //an empty request should reflect as a null designator
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = databaseManager.findByFileDesignator(request.getFileDesignator().toString());

        if(searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //else refer to score and get the metadata
        ScoreEntity score = searchResults.get();

        //key signature
        KeySignature keySignature = new KeySignature(score.getKeySignature());

        //metre
        String metre = score.getTimeSignature();
        int numBeats = Integer.parseInt(metre.substring(0, metre.indexOf("/")));
        int beatValue = Integer.parseInt(metre.substring(metre.indexOf("/")+1));
        TimeSignature timeSignature = new TimeSignature(numBeats,beatValue);

        //tempo
        TempoIndication tempoIndication = new TempoIndication(score.getTempoIndication());

        return new GetPieceMetadataResponse(keySignature,timeSignature,tempoIndication);
    }

    /**Used to retrieve a list of all track sequences of an interpreted work
     * @param request encapsulates a request with the designator of the work
     * @return an object encapsulating a map between sequence indices and their instruments
     * @throws InvalidDesignatorException if the designator does not correspond to an existing file
     */
    @Transactional
    @Override
    public GetTrackInfoResponse getTrackInfo(GetTrackInfoRequest request) throws InvalidDesignatorException {

        if(request==null)
            //an empty request should reflect as a null designator
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = databaseManager.findByFileDesignator(request.getFileDesignator().toString());

        if(searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //get the score and its associated tracks
        ScoreEntity score = searchResults.get();
        List<TrackEntity> tracks = score.getTracks();

        //create a response and add the appropriate tracks
        GetTrackInfoResponse getTrackInfoResponse = new GetTrackInfoResponse();
        for(TrackEntity track: tracks){
            byte index = (byte) tracks.indexOf(track);
            String trackName = track.getInstrumentName();
            getTrackInfoResponse.addTrack(index,trackName);
        }

        return getTrackInfoResponse;
    }


     /**Used to retrieve the Metadata of an arbitrary track within an existing interpreted piece
     * @param request encapsulates a request with the designator of the work and the index of the track
     * @return an object encapsulating a sequence of note objects corresponding to the tracks
     * @throws InvalidDesignatorException if the designator does not correspond to an existing file
     * @throws InvalidTrackException if the track index does not exist
     */
    @Transactional
    @Override
    public GetTrackMetadataResponse getTrackMetadata(GetTrackMetadataRequest request) throws InvalidDesignatorException, InvalidTrackException {

        if(request==null)
            //an empty request should reflect as a null designator
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = databaseManager.findByFileDesignator(request.getFileDesignator().toString());

        if(searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //get the index of the requested track, the score and the corresponding tracks
        byte trackIndex = request.getTrackIndex();
        ScoreEntity score = searchResults.get();
        List<TrackEntity> tracks = score.getTracks();

        if(trackIndex >= tracks.size() || trackIndex < 0)
            //cannot refer to a track that does not exist
            throw new InvalidTrackException(configurations.configuration(ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT));


        //get a rich text version of the track
        TrackEntity track = tracks.get(trackIndex);
        String richString = track.getRichTextNotes();

        return new GetTrackMetadataResponse(richString);
    }

    /**Used to retrieve a pitch summary of an arbitrary track within an existing interpreted piece
     * @param request encapsulates a request with the designator of the work and the index of the track
     * @return an object encapsulating a sequence of basic note objects corresponding to the tracks
     * @throws InvalidDesignatorException if the designator does not correspond to an existing file
     * @throws InvalidTrackException if the track index does not exist
     */
    @Transactional
    @Override
    public GetTrackOverviewResponse getTrackOverview(GetTrackOverviewRequest request) throws InvalidDesignatorException, InvalidTrackException {

        if(request==null)
            //an empty request should reflect as a null designator
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.EMPTY_REQUEST_EXCEPTION_TEXT));

        //search the repository for the piece with that designator
        Optional<ScoreEntity> searchResults = databaseManager.findByFileDesignator(request.getFileDesignator().toString());

        if(searchResults.isEmpty())
            //no such file exists - has yet to be interpreted
            throw new InvalidDesignatorException(configurations.configuration(ConfigurationName.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT));

        //get the track index requested, the score and corresponding track
        byte trackIndex = request.getTrackIndex();
        ScoreEntity score = searchResults.get();
        List<TrackEntity> tracks = score.getTracks();

        if(trackIndex >= tracks.size() || trackIndex < 0)
            //cannot refer to a track that does not exist
            throw new InvalidTrackException(configurations.configuration(ConfigurationName.INVALID_TRACK_INDEX_EXCEPTION_TEXT));

        //get a text overview of the track
        TrackEntity track = tracks.get(trackIndex);
        
        return new GetTrackOverviewResponse(track.getNoteSummary());
    }

}
