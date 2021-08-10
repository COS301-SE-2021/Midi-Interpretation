package com.noxception.midisense.interpreter.repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** Class that represents an entity equivalent of the {@link com.noxception.midisense.interpreter.parser.Track} class
 * that can be saved as a record in a CRUD repository
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
@Entity
public class TrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long trackID;

    @ElementCollection(fetch = FetchType.EAGER)
    private final List<byte[]> notes = new ArrayList<>();

    private String instrumentName;

    public TrackEntity() {
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Track}
     */
    public Long getTrackID() {
        return trackID;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Track}
     */
    public List<byte[]> getNotes() {
        return notes;
    }

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Track}
     */
    public String getInstrumentName() {
        return instrumentName;
    }

    /** A method that allocates a string of interpreted notes to a lookup table of note BLOB objects
     * where windows of 255 bytes are used
     *
     * @param notes the interpreted string of note data
     */
    public void setNotes(String notes) {
        byte[] inArray = notes.getBytes();
        int len = inArray.length;
        int segmentSize = 255;
        int i = 0;
        int window = len/segmentSize;
        while (i<= window){
            if (i != window){
                byte[] portion = Arrays.copyOfRange(inArray,i*segmentSize,(i+1)*segmentSize);
                this.notes.add(portion);
            }
            else{
                byte[] portion = Arrays.copyOfRange(inArray,i*segmentSize,len);
                this.notes.add(portion);
            }
            i++;
        }
    }

    /** Method to reconstruct a string of interpreted notes based on windows of byte arrays stored in the repository
     *
     * @return the interpreted string of notes corresponding to the collection of BLOB data
     */
    public String getRichTextNotes(){
        List<Byte> reconstruct = new ArrayList<>();
        for(byte[] part: notes){
            for(byte b: part) reconstruct.add(b);
        }
        byte[] response = new byte[reconstruct.size()];
        for(int i=0; i<reconstruct.size(); i++) response[i] = reconstruct.get(i);
        return new String(response);

    }

    /**
     * Is a condensed version of the corresponding method of {@link com.noxception.midisense.interpreter.parser.Track}
     */
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

    /**
     * See corresponding method of {@link com.noxception.midisense.interpreter.parser.Track}
     */
    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

}
