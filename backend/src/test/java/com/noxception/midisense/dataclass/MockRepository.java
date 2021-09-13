package com.noxception.midisense.dataclass;

import com.noxception.midisense.interpreter.parser.*;
import com.noxception.midisense.repository.DatabaseManager;
import com.noxception.midisense.repository.ScoreEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that mocks the functionality of {@link com.noxception.midisense.repository.ScoreRepository} repository class.
 * The operations are accessed in the same manner, due to the {@link DatabaseManager} wrapper from which both classes
 * inherit.
 */
public class MockRepository extends DatabaseManager {

    List<ScoreEntity> database = new ArrayList<>();

    //TODO: Claudio find a way to load and save scores. Maybe try saving before you need to make calls:
    // Hint: generate a temp midi file and use parseJSON to
    // generate a score to save, before you need to find by designator

    @Override
    public Optional<ScoreEntity> findByFileDesignator(String fileDesignator) {
        for(ScoreEntity s: database){
            if(s.getFileDesignator().equals(fileDesignator))
                return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public ScoreEntity save(ScoreEntity s) {
        database.add(s);
        return s;
    }

    public void generateMockScore(UUID fileDesignator){
        //mock the database with that designator
        Score s = new Score();
        ScoreEntity testEntity = new ScoreEntity();
        testEntity.setFileDesignator(fileDesignator.toString());

        //Add key signature to score
        KeySignature keySignature = new KeySignature();
        keySignature.tick =0;
        keySignature.commonName = "Cb";
        s.KeySignatureMap = new ArrayList<>();
        s.KeySignatureMap.add(keySignature);

        //Add tempo indication to score
        TempoIndication tempoIndication = new TempoIndication();
        tempoIndication.tick=0;
        tempoIndication.tempoIndication = 70;
        s.TempoIndicationMap = new ArrayList<>();
        s.TempoIndicationMap.add(tempoIndication);

        //Add time signature beat value and number of beats to score
        TimeSignature timeSignature = new TimeSignature();
        TimeSignature.InnerTime innerTime = new TimeSignature.InnerTime();
        innerTime.beatValue =4;
        innerTime.numBeats=4;
        timeSignature.tick=0;
        timeSignature.time= innerTime;
        s.TimeSignatureMap = new ArrayList<>();
        s.TimeSignatureMap.add(timeSignature);

        Channel channel = new Channel();
        channel.channelNumber = 1;
        channel.instrument = "Electric Bass (pick)";
        channel.ticksPerBeat = 92;
        Track track = new Track();
        track.tick = 0;
        track.notes = new ArrayList<>();
        Note note = new Note();
        note.duration = 600;
        note.offVelocity = 0;
        note.onVelocity = 100;
        note.value = 69;
        track.notes.add(note);
        channel.trackMap = new ArrayList<>();
        channel.trackMap.add(track);
        s.channelList = new ArrayList<>();
        s.channelList.add(channel);

        testEntity.encodeScore(s);
        this.save(testEntity);
    }
}
