package com.noxception.midisense.interpreter.repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Class that represents an entity equivalent of the {@link com.noxception.midisense.interpreter.parser.Score} class
 * that can be saved as a record in a CRUD repository, with one or more additional methods for referring to the a file designator
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */

@Entity
public class ScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long scoreID;

    private String fileDesignator;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TrackEntity> tracks = new ArrayList<>();

    private String keySignature;
    private String timeSignature;
    private int tempoIndication;

    public ScoreEntity() {
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public void addTrack(TrackEntity trackEntity){
        tracks.add(trackEntity);
    }

    /**
     * @return the unique integer corresponding to the score entity in the external repository
     */
    public Long getScoreID() {
        return scoreID;
    }

    /**
     * @return the unique identifier corresponding to the original midi file, in the external repository
     */
    public String getFileDesignator() {
        return fileDesignator;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public List<TrackEntity> getTracks() {
        return tracks;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public String getKeySignature() {
        return keySignature;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public String getTimeSignature() {
        return timeSignature;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public int getTempoIndication() {
        return tempoIndication;
    }

    /**
     * @param fileDesignator the unique identifier corresponding to the original midi file, in the external repository
     */
    public void setFileDesignator(String fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public void setTracks(List<TrackEntity> tracks) {
        this.tracks = tracks;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public void setKeySignature(String keySignature) {
        this.keySignature = keySignature;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public void setTimeSignature(String timeSignature) {
        this.timeSignature = timeSignature;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Score}
     */
    public void setTempoIndication(int tempoIndication) {
        this.tempoIndication = tempoIndication;
    }
}
