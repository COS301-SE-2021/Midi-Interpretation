package com.noxception.midisense.interpreter.parser;

import com.noxception.midisense.config.dataclass.LoggableObject;
import org.jfugue.parser.ParserListener;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.HashMap;

public class MIDISenseParserListener extends LoggableObject implements ParserListener{
    private final HashMap<Integer, ArrayList<String>> trackMap = new HashMap<>();
    private int trackIndex = -1;
    private int barIndex = -1;

    public MIDISenseParserListener() {
        super();
    }

    @Override
    public void onNoteParsed(Note note) {
    }

    @Override
    public void onChordParsed(Chord chord) {
        log(chord.toString());
    }

    @Override
    public void beforeParsingStarts() {

    }

    @Override
    public void afterParsingFinished() {

    }

    @Override
    public void onTrackChanged(byte track) {
        this.trackIndex = track;
        log(String.format("In track %d",this.trackIndex+1));
        trackMap.put(this.trackIndex,new ArrayList<>());
    }

    @Override
    public void onLayerChanged(byte b) {
        log(String.format("In Layer %d",b));
    }

    @Override
    public void onInstrumentParsed(byte b) {
        log(String.format("Instrument %d",b));
    }

    @Override
    public void onTempoChanged(int i) {
        log(String.format("Tempo Change %d",i));
    }

    @Override
    public void onKeySignatureParsed(byte b, byte b1) {
        log(String.format("Key Signature %d / %d",b, b1));
    }

    @Override
    public void onTimeSignatureParsed(byte b, byte b1) {
        log(String.format("Time Signature %d / %d",b, b1));
    }

    @Override
    public void onBarLineParsed(long l) {
        barIndex++;
        log(String.format("In bar %d of track %d",this.barIndex+1,this.trackIndex+1));
    }

    @Override
    public void onTrackBeatTimeBookmarked(String s) {

    }

    @Override
    public void onTrackBeatTimeBookmarkRequested(String s) {

    }

    @Override
    public void onTrackBeatTimeRequested(double v) {

    }

    @Override
    public void onPitchWheelParsed(byte b, byte b1) {

    }

    @Override
    public void onChannelPressureParsed(byte b) {

    }

    @Override
    public void onPolyphonicPressureParsed(byte b, byte b1) {

    }

    @Override
    public void onSystemExclusiveParsed(byte... bytes) {

    }

    @Override
    public void onControllerEventParsed(byte b, byte b1) {

    }

    @Override
    public void onLyricParsed(String s) {
        log(String.format("Found lyrics %s",s));
    }

    @Override
    public void onMarkerParsed(String s) {

    }

    @Override
    public void onFunctionParsed(String s, Object o) {

    }

    @Override
    public void onNotePressed(Note note) {
        ArrayList<String> notes = trackMap.get(this.trackIndex);
        log(String.format("Found note %s",Note.getToneString(note.getValue())));
        notes.add(Note.getToneString(note.getValue()) + note.getVelocityString());
    }

    @Override
    public void onNoteReleased(Note note) {

    }

    public HashMap<Integer, ArrayList<String>> getTrackMap() {
        return trackMap;
    }


}
