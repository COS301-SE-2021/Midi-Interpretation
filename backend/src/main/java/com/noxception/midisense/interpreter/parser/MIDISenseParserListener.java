package com.noxception.midisense.interpreter.parser;

import org.jfugue.integration.MusicXmlParserListener;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.HashMap;

public class MIDISenseParserListener extends MusicXmlParserListener {
    private final HashMap<Integer, ArrayList<String>> trackMap = new HashMap<>();
    private int index = -1;

    public MIDISenseParserListener() {
        super();
    }

    @Override
    public void onNoteParsed(Note note) {
        ArrayList<String> notes = trackMap.get(this.index);
        notes.add(Note.getToneString(note.getValue()) + note.getVelocityString());
    }

    @Override
    public void onTrackChanged(byte track) {
        index++;
        trackMap.put(this.index,new ArrayList<>());
    }

    public HashMap<Integer, ArrayList<String>> getTrackMap() {
        return trackMap;
    }


}
