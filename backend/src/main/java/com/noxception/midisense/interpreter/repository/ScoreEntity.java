package com.noxception.midisense.interpreter.repository;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class ScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long scoreID;
    private String fileDesignator;
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

    public Long getScoreID() {
        return scoreID;
    }

    public String getFileDesignator() {
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

    public void setFileDesignator(String fileDesignator) {
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
