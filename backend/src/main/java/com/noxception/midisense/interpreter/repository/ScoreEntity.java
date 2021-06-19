package com.noxception.midisense.interpreter.repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class ScoreEntity {
    @Id
    @GeneratedValue
    private UUID scoreID;
    private UUID fileDesignator;
    @OneToMany(cascade = CascadeType.ALL)
    private List<TrackEntity> tracks = new ArrayList<>();
    private String keySignature;
    private String timeSignature;
    private int tempoIndication;

    public ScoreEntity() {
    }

    public void addTrack(TrackEntity trackEntity){
        tracks.add(trackEntity);
    }

    public UUID getScoreID() {
        return scoreID;
    }

    public UUID getFileDesignator() {
        return fileDesignator;
    }

    public List<TrackEntity> getTracks() {
        return tracks;
    }

    public String getKeySignature() {
        return keySignature;
    }

    public String getTimeSignature() {
        return timeSignature;
    }

    public int getTempoIndication() {
        return tempoIndication;
    }

    public void setFileDesignator(UUID fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public void setTracks(List<TrackEntity> tracks) {
        this.tracks = tracks;
    }

    public void setKeySignature(String keySignature) {
        this.keySignature = keySignature;
    }

    public void setTimeSignature(String timeSignature) {
        this.timeSignature = timeSignature;
    }

    public void setTempoIndication(int tempoIndication) {
        this.tempoIndication = tempoIndication;
    }
}
