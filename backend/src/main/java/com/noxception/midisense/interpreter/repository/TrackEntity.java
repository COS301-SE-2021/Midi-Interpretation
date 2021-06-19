package com.noxception.midisense.interpreter.repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
public class TrackEntity {
    @Id
    @GeneratedValue
    private UUID trackID;
    @ElementCollection
    private List<byte[]> notes = new ArrayList<>();
    private String instrumentName;

    public TrackEntity() {
    }

    public UUID getTrackID() {
        return trackID;
    }

    public List<byte[]> getNotes() {
        return notes;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setNotes(String notes) {
        byte[] inArray = notes.getBytes();
        int len = inArray.length;
        int segmentSize = 255;
        int i = 0;
        while(i<len/segmentSize){
            byte[] portion = Arrays.copyOfRange(inArray,i*segmentSize,(i+1)*segmentSize);
            this.notes.add(portion);
            i++;
        }
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
