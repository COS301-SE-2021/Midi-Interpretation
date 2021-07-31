package com.noxception.midisense.interpreter.repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int window = len/segmentSize;
        while(i<= window){
            if(i != window){
                byte[] portion = Arrays.copyOfRange(inArray,i*segmentSize,(i+1)*segmentSize);
                this.notes.add(portion);
            }
            else{
                byte[] portion = Arrays.copyOfRange(inArray,i*segmentSize,len-1);
                this.notes.add(portion);
            }
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

    public List<String> getNoteSummary(){
        String stepSensitive = "\"step\": \"";
        String octaveSensitive = "\"octave\": ";
        String sepSensitive = "\", ";
        String sep2Sensitive = ", ";

        List<String> newList = new ArrayList<>();

        String richString = getRichTextNotes();
        int lastIndex = richString.indexOf(stepSensitive);
        while(lastIndex != -1){
            int sepPos = richString.indexOf(sepSensitive,lastIndex);
            String step = richString.substring(lastIndex+stepSensitive.length(),sepPos);
            sepPos = richString.indexOf(sep2Sensitive,sepPos+2);
            int octavePos = richString.indexOf(octaveSensitive,lastIndex);
            String octave = richString.substring(octavePos+octaveSensitive.length(),sepPos);
            newList.add(step+octave);
            lastIndex = richString.indexOf(stepSensitive,lastIndex+1);
        }
        return newList;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
