package com.noxception.midisense.interpreter.parser;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jfugue.parser.ParserListener;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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
@Slf4j
public class MIDISenseParserListener implements ParserListener{

    private Score parsedScore;
    private String filename;
    private StandardConfig configurations;

    public MIDISenseParserListener(String filename, StandardConfig config) {
        super();
        this.filename = filename;
        this.configurations = config;
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
     * @param channel the track index switched to during parsing
     */
    @Override
    public void onTrackChanged(byte channel) {
        //switch tracks
        if (parsedScore.getTrack(channel)==null){
            //if the parsed score doesn't have the track, add it

            String trackData = "{}";
            //gather data by calling python script
            try{
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "python",
                        "\""+configurations.configuration(ConfigurationName.MIDI_INTERPRETATION_SCRIPT_PATH)+"\"",
                        String.format("\"%s\"",filename),
                        String.valueOf(channel));

                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();
                InputStream stream = process.getInputStream();
                InputStream errorStream = process.getErrorStream();

                StringWriter errorWriter = new StringWriter();
                IOUtils.copy(errorStream, errorWriter, StandardCharsets.UTF_8);
                String errorData = errorWriter.toString();
                if(!errorData.equals("")){
                    log.error(errorData);
                }

                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
                trackData = writer.toString().replace("\r\n","");
            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
            parsedScore.addTrack(channel,trackData);
        }

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
    public void onNoteParsed(Note note){

    }

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
    public void onInstrumentParsed(byte b) {

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
