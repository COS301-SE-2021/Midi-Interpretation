package com.noxception.midisense.interpreter.parser;

import org.jfugue.parser.ParserListener;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

/**
 * Class used for the implementation of responses to invocations made by the jFugue 5.0.9 Midi Parser.
 * Events raised by the Parser are delegated to an object of this class to be dealt with.
 *
 * Appropriate usage of the Parser and its listener are as follows:
 *
 *
 * <pre class="code">
 * //create a parser and corresponding listener
 * MidiParser parser = new MidiParser();
 * MIDISenseParserListener listener = new MIDISenseParserListener();
 * parser.addParserListener(listener);
 *
 * //start parsing
 * parser.parse(MidiFileManager.load(sourceFile));
 *
 * //save the resultant score
 * Score score = listener.getParsedScore();
 * </pre>
 *
 * @author Adrian Rae
 * @since 1.0.0
 */
public class MIDISenseParserListener implements ParserListener{


    private int trackIndex = -1;

    private Score parsedScore;

    public MIDISenseParserListener() {
        super();
    }

    /** Method to access the score once parsing has occured
     *
     * @return the score after parsing has occurred
     */
    public Score getParsedScore() {
        return parsedScore;
    }

    /**
     * The content invoked before parsing commences. Creates a new score for parsing.
     */
    @Override
    public void beforeParsingStarts() {
        parsedScore = new Score();
    }

    /**
     * The content invoked when the parser switches from one track to another.
     * Switches tracks on the parsed score and creates new tracks if they don't already exist
     *
     * @param track the track index switched to during parsing
     */
    @Override
    public void onTrackChanged(byte track) {
        //switch tracks
        this.trackIndex = track;
        if (parsedScore.getTrack(track)==null)
            //if the parsed score doesn't have the track, add it
            parsedScore.addTrack(track);
    }

    /**
     * The content invoked when a new instrument control sequence is parsed.
     * Assigns the current instrument to the current track
     *
     * @param b the index of the instrument
     */
    @Override
    public void onInstrumentParsed(byte b) {
        parsedScore.getTrack(trackIndex).setInstrument(b);
    }

    /**
     * The content invoked when a new tempo control sequence is parsed.
     * Assigns the current tempo indication to the score
     *
     * @param i the tempo of the work
     */
    @Override
    public void onTempoChanged(int i) {
        parsedScore.setTempoIndication(i);
    }

    /** The content invoked when a new note is parsed.
     *  Adds the note to the parsed score
     *
     * @param note the note encountered while parsing
     */
    @Override
    public void onNoteParsed(Note note){
        parsedScore.getTrack(trackIndex).addNote(note);
    }

    /**
     * The content invoked when a key Signature control sequence is parsed.
     * Assigns the current key signature to the score
     *
     * @param b the tonal centre of the signature
     * @param b1 the key of the signature
     */
    @Override
    public void onKeySignatureParsed(byte b, byte b1) {
        parsedScore.setKeySignature(b,b1);
    }

    /**
     * The content invoked when a new time signature control sequence is parsed.
     * Assigns the current time signature to the score
     *
     * @param b the beat value
     * @param b1 the beat duration
     */
    @Override
    public void onTimeSignatureParsed(byte b, byte b1) {
        parsedScore.setTimeSignature(b,b1);
    }

    //=================================
    // UNIMPLEMENTED
    //=================================

    @Override
    public void onChordParsed(Chord chord) {
    }


    @Override
    public void afterParsingFinished() {

    }

    @Override
    public void onLayerChanged(byte b) {
    }

    @Override
    public void onBarLineParsed(long l) {
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


}
