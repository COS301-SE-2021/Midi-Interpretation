package com.noxception.midisense.interpreter.repository;

import org.apache.commons.lang3.ArrayUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
public class TrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long trackID;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<byte[]> notes = new ArrayList<>();
    private String instrumentName;

    public TrackEntity() {
    }

    public Long getTrackID() {
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

    public String getRichTextNotes(){
        List<Byte> reconstruct = new ArrayList<>();
        for(byte[] part: notes){
            for(byte b: part) reconstruct.add(b);
        }
        byte[] response = new byte[reconstruct.size()];
        for(int i=0; i<reconstruct.size(); i++) response[i] = reconstruct.get(i);
        return new String(response);

    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
