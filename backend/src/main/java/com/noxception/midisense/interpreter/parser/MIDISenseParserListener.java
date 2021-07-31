package com.noxception.midisense.interpreter.parser;

import com.noxception.midisense.config.dataclass.LoggableObject;
import org.jfugue.parser.ParserListener;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

public class MIDISenseParserListener extends LoggableObject implements ParserListener{
    private int trackIndex = -1;

    private Score parsedScore;

    public MIDISenseParserListener() {
        super();
    }

    @Override
    public void onNoteParsed(Note note){
        parsedScore.getTrack(trackIndex).addNote(note);
        log("Parsed Note: "+note);
    }

    @Override
    public void onChordParsed(Chord chord) {
    }

    @Override
    public void beforeParsingStarts() {
        parsedScore = new Score();
    }

    @Override
    public void afterParsingFinished() {

    }

    @Override
    public void onTrackChanged(byte track) {
        this.trackIndex = track;
        if(parsedScore.getTrack(track)==null) parsedScore.addTrack(track);
        log("IN TRACK: "+track);
    }

    @Override
    public void onLayerChanged(byte b) {
    }

    @Override
    public void onInstrumentParsed(byte b) {
        parsedScore.getTrack(trackIndex).setInstrument(b);
    }

    @Override
    public void onTempoChanged(int i) {
        parsedScore.setTempoIndication(i);
    }

    @Override
    public void onKeySignatureParsed(byte b, byte b1) {
        parsedScore.setKeySignature(b,b1);
    }

    @Override
    public void onTimeSignatureParsed(byte b, byte b1) {
        parsedScore.setTimeSignature(b,b1);
    }

    @Override
    public void onBarLineParsed(long l) {
    }

    @Override
    public void onTrackBeatTimeBookmarked(String s) {
        log(s);
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
    }

    @Override
    public void onMarkerParsed(String s) {

    }

    @Override
    public void onFunctionParsed(String s, Object o) {

    }

    @Override
    public void onNotePressed(Note note) {
    }

    @Override
    public void onNoteReleased(Note note) {

    }

    public Score getParsedScore() {
        return parsedScore;
    }
}
