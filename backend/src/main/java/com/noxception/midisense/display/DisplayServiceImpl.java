package com.noxception.midisense.display;

import com.noxception.midisense.config.DevelopmentNote;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.config.dataclass.LoggableObject;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.InterpreterServiceImpl;
import com.noxception.midisense.interpreter.dataclass.KeySignature;
import com.noxception.midisense.interpreter.dataclass.TempoIndication;
import com.noxception.midisense.interpreter.dataclass.TimeSignature;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.repository.ScoreEntity;
import com.noxception.midisense.interpreter.repository.ScoreRepository;
import com.noxception.midisense.interpreter.repository.TrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DisplayServiceImpl extends LoggableObject implements DisplayService{

    @Autowired
    InterpreterServiceImpl interpreterService;

    @Autowired
    ScoreRepository scoreRepository;

    @DevelopmentNote(
            taskName = "getPieceMetadata Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/19 22:37",
            comments = "Added Method."
    )
    @Transactional
    @Override
    public GetPieceMetadataResponse getPieceMetadata(GetPieceMetadataRequest request) throws InvalidDesignatorException {
        //METHOD 1 - EXISTING INTERPRETER METHODS
//        if(request==null) throw new InvalidDesignatorException(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT);
//        UUID fileDesignator = request.getFileDesignator();
//        TempoIndication tempo = interpreterService.interpretTempo(new InterpretTempoRequest(fileDesignator)).getTempo();
//        TimeSignature timeSignature = interpreterService.interpretMetre(new InterpretMetreRequest(fileDesignator)).getMetre();
//        KeySignature keySignature = interpreterService.interpretKeySignature(new InterpretKeySignatureRequest(fileDesignator)).getKeySignature();
//        return new GetPieceMetadataResponse(keySignature,timeSignature,tempo);
        //METHOD 2 - NEW PERSISTENCE LOOKUP
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
        ScoreEntity score = searchResults.get();
        KeySignature keySignature = new KeySignature(score.getKeySignature());
        String metre = score.getTimeSignature();
        int numBeats = Integer.parseInt(metre.substring(0, metre.indexOf("/")));
        int beatValue = Integer.parseInt(metre.substring(metre.indexOf("/")+1));
        TimeSignature timeSignature = new TimeSignature(numBeats,beatValue);
        TempoIndication tempoIndication = new TempoIndication(score.getTempoIndication());
        return new GetPieceMetadataResponse(keySignature,timeSignature,tempoIndication);
    }

    @DevelopmentNote(
            taskName = "getTrackInfo Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.PENDING_REVIEW,
            lastModified = "2021/06/19 22:37",
            comments = "Added Method."
    )
    @Transactional
    @Override
    public GetTrackInfoResponse getTrackInfo(GetTrackInfoRequest request) throws InvalidDesignatorException {
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
        GetTrackInfoResponse getTrackInfoResponse = new GetTrackInfoResponse();
        ScoreEntity score = searchResults.get();
        List<TrackEntity> tracks = score.getTracks();
        for(TrackEntity track: tracks){
            byte index = (byte) tracks.indexOf(track);
            String trackName = track.getInstrumentName();
            getTrackInfoResponse.addTrack(index,trackName);
        }
        return getTrackInfoResponse;
    }

    @DevelopmentNote(
            taskName = "getTrackMetadata Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.IN_PROGRESS,
            lastModified = "2021/06/19 22:37",
            comments = "Added Method."
    )
    @Transactional
    @Override
    public GetTrackMetadataResponse getTrackMetadata(GetTrackMetadataRequest request) throws InvalidDesignatorException, InvalidTrackException {
        Optional<ScoreEntity> searchResults = scoreRepository.findByFileDesignator(request.getFileDesignator().toString());
        if(searchResults.isEmpty()) throw new InvalidDesignatorException(MIDISenseConfig.FILE_DOES_NOT_EXIST_EXCEPTION_TEXT);
        byte trackIndex = request.getTrackIndex();
        ScoreEntity score = searchResults.get();
        List<TrackEntity> tracks = score.getTracks();
        if(trackIndex >= tracks.size()) throw new InvalidTrackException(MIDISenseConfig.INVALID_TRACK_INDEX_EXCEPTION_TEXT);
        TrackEntity track = tracks.get(trackIndex);
        String richString = track.getRichTextNotes();
        return new GetTrackMetadataResponse(richString);
    }

    @DevelopmentNote(
            taskName = "getPieceOverview Use Case",
            developers = {DevelopmentNote.Developers.ADRIAN},
            status = DevelopmentNote.WorkState.IN_PROGRESS,
            lastModified = "2021/06/19 22:37",
            comments = "Added Method."
    )
    @Transactional
    @Override
    public GetTrackOverviewResponse getPieceOverview(GetTrackOverviewRequest request) {
        return null;
    }
}
