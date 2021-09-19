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
    private final int mockTick = 0;
    private final String mockKey = "Cb";
    private final int mockTempo = 70;
    private final int mockNumBeats = 4;
    private final int mockBeatValue = 4;
    private final int mockChannelNumber = 1;
    private final String mockInstrument = "Electric Bass (pick)";
    private final int mockTicksPerBeat = 192;
    private final int mockValue = 69;
    private final int mockOnVelocity = 100;
    private final int mockOffVelocity = 0;
    private final int mockDuration = 600;

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
        keySignature.tick = this.mockTick;
        keySignature.commonName = this.mockKey;
        s.KeySignatureMap = new ArrayList<>();
        s.KeySignatureMap.add(keySignature);

        //Add tempo indication to score
        TempoIndication tempoIndication = new TempoIndication();
        tempoIndication.tick = this.mockTick;
        tempoIndication.tempoIndication = this.mockTempo;
        s.TempoIndicationMap = new ArrayList<>();
        s.TempoIndicationMap.add(tempoIndication);

        //Add time signature beat value and number of beats to score
        TimeSignature timeSignature = new TimeSignature();
        TimeSignature.InnerTime innerTime = new TimeSignature.InnerTime();
        innerTime.beatValue =this.mockBeatValue;
        innerTime.numBeats= this.mockNumBeats;
        timeSignature.tick=this.mockTick;
        timeSignature.time= innerTime;
        s.TimeSignatureMap = new ArrayList<>();
        s.TimeSignatureMap.add(timeSignature);

        //Add channel Details
        Channel channel = new Channel();
        channel.channelNumber = this.mockChannelNumber;
        channel.instrument = this.mockInstrument;
        channel.ticksPerBeat = this.mockTicksPerBeat;
        Track track = new Track();
        track.tick = this.mockTick;
        track.notes = new ArrayList<>();
        Note note = new Note();
        note.duration = this.mockDuration;
        note.offVelocity = this.mockOffVelocity;
        note.onVelocity = this.mockOnVelocity;
        note.value = this.mockValue;
        track.notes.add(note);
        channel.trackMap = new ArrayList<>();
        channel.trackMap.add(track);
        s.channelList = new ArrayList<>();
        s.channelList.add(channel);

        testEntity.encodeScore(s);
        this.save(testEntity);
    }

    //Getters and Setters


    public int getMockTick() {
        return mockTick;
    }

    public String getMockKey() {
        return mockKey;
    }

    public int getMockTempo() {
        return mockTempo;
    }

    public int getMockNumBeats() {
        return mockNumBeats;
    }

    public int getMockBeatValue() {
        return mockBeatValue;
    }

    public int getMockChannelNumber() {
        return mockChannelNumber;
    }

    public String getMockInstrument() {
        return mockInstrument;
    }

    public int getMockTicksPerBeat() {
        return mockTicksPerBeat;
    }

    public int getMockValue() {
        return mockValue;
    }

    public int getMockOnVelocity() {
        return mockOnVelocity;
    }

    public int getMockOffVelocity() {
        return mockOffVelocity;
    }

    public int getMockDuration() {
        return mockDuration;
    }
}
