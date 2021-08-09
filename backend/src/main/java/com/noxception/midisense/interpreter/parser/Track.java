package com.noxception.midisense.interpreter.parser;

import com.google.common.base.CaseFormat;
import org.jfugue.theory.Note;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** A class that encapsulates a high-level representation of an individual track sequence (instrument line) of a midi file.
 * The class provides methods that allow for a high-level interaction with musical elements and not low-level sequence data of a midi file
 *
 * For more information on parsing, see {@link MIDISenseParserListener}
 * @author Adrian Rae
 * @since 1.0.0
 */
public class Track {

    private final List<Note> noteSequence = new ArrayList<>();
    private byte instrumentIndex;

    /**
     * A lookup between standard midi file instrument indices and their names
     */
    private final static String[] instruments = {"Acoustic Grand Piano","Bright Acoustic Piano","Electric Grand Piano","Honky-tonk Piano","Electric Piano 1","Electric Piano 2","Harpsichord","Clavi","Celesta","Glockenspiel","Music Box","Vibraphone","Marimba","Xylophone","Tubular Bells","Dulcimer","Drawbar Organ","Percussive Organ","Rock Organ","Church Organ","Reed Organ","Accordion","Harmonica","Tango Accordion","Acoustic Guitar (nylon)","Acoustic Guitar (steel)","Electric Guitar (jazz)","Electric Guitar (clean)","Electric Guitar (muted)","Overdriven Guitar","Distortion Guitar","Guitar harmonics","Acoustic Bass","Electric Bass (finger)","Electric Bass (pick)","Fretless Bass","Slap Bass 1","Slap Bass 2","Synth Bass 1","Synth Bass 2","Violin","Viola","Cello","Contrabass","Tremolo Strings","Pizzicato Strings","Orchestral Harp","Timpani","String Ensemble 1","String Ensemble 2","SynthStrings 1","SynthStrings 2","Choir Aahs","Voice Oohs","Synth Voice","Orchestra Hit","Trumpet","Trombone","Tuba","Muted Trumpet","French Horn","Brass Section","SynthBrass 1","SynthBrass 2","Soprano Sax","Alto Sax","Tenor Sax","Baritone Sax","Oboe","English Horn","Bassoon","Clarinet","Piccolo","Flute","Recorder","Pan Flute","Blown Bottle","Shakuhachi","Whistle","Ocarina","Lead 1 (square)","Lead 2 (sawtooth)","Lead 3 (calliope)","Lead 4 (chiff)","Lead 5 (charang)","Lead 6 (voice)","Lead 7 (fifths)","Lead 8 (bass + lead)","Pad 1 (new age)","Pad 2 (warm)","Pad 3 (polysynth)","Pad 4 (choir)","Pad 5 (bowed)","Pad 6 (metallic)","Pad 7 (halo)","Pad 8 (sweep)","FX 1 (rain)","FX 2 (soundtrack)","FX 3 (crystal)","FX 4 (atmosphere)","FX 5 (brightness)","FX 6 (goblins)","FX 7 (echoes)","FX 8 (sci-fi)","Sitar","Banjo","Shamisen","Koto","Kalimba","Bag pipe","Fiddle","Shanai","Tinkle Bell","Agogo","Steel Drums","Woodblock","Taiko Drum","Melodic Tom","Synth Drum","Reverse Cymbal","Guitar Fret Noise","Breath Noise","Seashore","Bird Tweet","Telephone Ring","Helicopter","Applause","Gunshot"};

    /**
     * @return the sequence of notes that comprise the Track
     */
    public List<Note> getNoteSequence() {
        return noteSequence;
    }

    /** Adds a note to the existing track
     *
     * @param note the note to be added to the sequence
     */
    public void addNote(Note note){
        noteSequence.add(note);
    }

    /**
     * @return the instrument index of the Track
     */
    public byte getInstrument() {
        return instrumentIndex;
    }

    /** Set the current instrument to a new index
     *
     * @param instrument the instrument index corresponding to the Track
     */
    public void setInstrument(byte instrument) {
        this.instrumentIndex = instrument;
    }

    /**
     * @return the string corresponding to the current instrument index
     */
    public String getInstrumentString(){
        return instruments[this.instrumentIndex];
    }

    /** A method that returns a high-level, hierarchical representation of a note
     *
     * @param note a note that is parsed by a Midi parser
     * @return the JSON-serialised representation of the note
     */
    private String noteToString(Note note){

        //create a list of note properties
        List<String> innerContent = new ArrayList<>();

        //get a list of note methods
        for (Method method: note.getClass().getMethods()){
                try {
                    //if there is a getter, call it
                    if (method.getName().contains("get") && method.getParameterCount()==0 && !method.getName().contains("Class")){
                        String field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,method.getName().substring(3));
                        field = "\""+field+"\"";
                        Object valueObject = method.invoke(note);
                        String value;
                        if (valueObject==null){
                            value = "";
                        }
                        else{
                            value = valueObject.toString();
                            if(!isInteger(value) && !isBoolean(value)){
                                value = "\""+value+"\"";
                            }
                        }
                        innerContent.add(field+ ":" + value);
                    }
                    else if (method.getName().contains("is") && method.getParameterCount()==0){
                        String field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,method.getName());
                        Object valueObject = method.invoke(note);
                        field = "\""+field+"\"";
                        String value;
                        if (valueObject==null){
                            value = "";
                        }
                        else{
                            value = valueObject.toString();
                            if(!isInteger(value) && !isBoolean(value)){
                                value = "\""+value+"\"";
                            }
                        }
                        innerContent.add(field+ ":" + value);
                    }
                }
                catch(IllegalAccessException | InvocationTargetException ignored){

                }
            }

        return String.format("{%s}",String.join(",",innerContent));
    }

    /** A batch method of noteToString
     *
     * @return the JSON-serialised representation of the note sequence in the Track
     */
    public String notesToString() {
        List<String> items = new ArrayList<>();
        for(Note n: noteSequence){
            items.add(noteToString(n));
        }
        String notes = String.join(", ",items);

        return String.format("{"+"\"notes\": [%s]"+"}",notes);
    }

    /** A method that returns a high-level, hierarchical representation of a Track
     *
     * @return the JSON-serialised representation of the Track
     */
    @Override
    public String toString() {
        List<String> items = new ArrayList<>();
        for(Note n: noteSequence){
            items.add(noteToString(n));
        }
        String notes = String.join(", ",items);

        return String.format("{ \"instrument\": \"%s\", \"notes\": [%s]}",getInstrumentString(),notes);
    }

    private boolean isInteger(String o){
        try{
            int i = Integer.parseInt(o);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    private boolean isBoolean(String o){
        return (o.toUpperCase().equals("TRUE")) || (o.toUpperCase().equals("FALSE"));
    }

}
