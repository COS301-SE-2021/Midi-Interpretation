import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Divider, Grid, Icon, IconButton, LinearProgress, Tooltip} from "@material-ui/core";
import Cookies from "universal-cookie";
import {withStyles} from "@material-ui/core/styles";
import { KeyboardShortcuts, MidiNumbers } from 'react-piano';
import '../services/styles.css';
import SoundfontProvider from '../services/SoundfontProvider';
import PianoWithRecording from '../services/PianoWithRecording';
import InstrumentListProvider from "../services/InstrumentListProvider";
import PianoConfig from "../services/PianoConfig";
import DimensionsProvider from "../services/DimensionsProvider";
import InstrumentMenu from "../../matx/components/InstrumentMenu";
import LiveSettings from "../../matx/components/LiveSettings"
import PianoRoll from "../../matx/components/PianoRoll";
import MIDISounds from 'midi-sounds-react';

/**
 * This class defines the interpretation of a midi sequence that has been supplied by the user
 * It displays:
 *
 *      - Control tab (live settings)
 *      - Piano with recording
 *      - Piano Config
 *      - Piano Roll
 *      - Track Viewer
 *          - Bar Information
 *              - Bar number
 *              - Chords
 *              - Bar events
 *              - Notes
 *                  - Pitch
 *                  - Velocity
 *                  - Octave Value
 */

class Live extends Component {

    /**
     * The main constructor for the Display view
     * The state values are defined here as well as the methods for the view
     *
     * @constructor
     * @param props
     */


    constructor(props) {
        super(props)

        // Initialize the cookie system
        this.cookies = new Cookies()

        this.isActive = false
        this.interval = null

        this.state = {
            recordedNotes: {},
            open:false,
            display: "",
            ticksPerBeat:1,
            midisenseService: new MidiSenseService(),
            color : [
                "#37A2DA",
                "#32C5E9",
                "#67E0E3",
                "#9FE6B8",
                "#FFDB5C",
                "#ff9f7f",
                "#fb7293",
                "#E062AE",
                "#E690D1",
                "#e7bcf3",
                "#9d96f5",
                "#8378EA",
            ],
            recording: {
                initialTime: null,
                elapsed: 0,
                wait: true,
                bpm:100,
                length:64,
                quanta:0,
                quantaLength:1/16,
                mode: 'STOP',
                active: "fiber_manual_record",
                color: "#d00000",
                timerInterval: 100
            },
            config: {
                instrumentName: 'accordion',
                noteRange: {
                    first: MidiNumbers.fromNote('c3'),
                    last: MidiNumbers.fromNote('f4'),
                },
                keyboardShortcutOffset: 0,
            },
            data:{
                trackData:[],
                ticksPerBeat:0,
                instrument:""
            },
        }
    }

    /**
     * When the component mounts this will initialize the audio volume to 10%
     */

    componentDidMount() {
        this.midiSounds.setMasterVolume(0.1);
    }

    //====================================
    // DISPLAY STATE VALUE SETTERS
    //====================================

    /**
     * setTrackData
     *
     * This is a setter used to populate the track array that is passed to the track viewer
     *
     * @param td
     */

    setTrackData = (td) => {

        this.setState({
            trackData: td
        })
    }

    /**
     * setInstrument
     *
     * This sets the current instrument. The config will change the type of midi instrument that the on screen piano uses
     *
     * @param i
     */

    setInstrument = (i) => {
        this.setState({
            config: {
                instrumentName: i,
                noteRange: {
                    first: this.state.config.noteRange.first,
                    last: this.state.config.noteRange.last,
                },
                keyboardShortcutOffset: this.state.config.keyboardShortcutOffset,
            }
        })
    }


    //====================================
    // DISPLAY METHODS
    //====================================

    /**
     * setRecording
     *
     * This sets the state.recording value without needing to go through the normal state procedures. This method is used in child components.
     *
     * @param value
     */

    setRecording = value => {
        this.setState({
            recording: Object.assign({}, this.state.recording, value),
        });
    };

    /**
     * onClickRecord
     *
     * This method handles the record button being pressed.
     * It calls start recording, starts the metronome and changes the display state so that the record button is reactive.
     */

    onClickRecord = () => {
        if(this.state.recording.mode === "STOP"){
            this.handleStart()
            this.playLoop()
            this.setRecording({
                mode: "RECORDING",
                active:"stop",
                color: "#555",
                initialTime: new Date().getTime(),
            })
        }
        else if(this.state.recording.mode === "RECORDING"){
            this.isActive = false
            this.stopLoop()
            this.setRecording({
                mode: "STOP",
                active:"fiber_manual_record",
                color: "#d00000",
                initialTime: null,
            })
        }
    }

    /**
     * onClickClear
     *
     * This method handles the clear button being pressed.
     * It resets the elapsed and initial time, clears the recorded notes, stops the metronome and changes the
     * display state so that the clear button is reactive.
     */

    onClickClear = () => {
        this.setRecording({elapsed:0, initialTime: null})

        this.setState({recordedNotes: {}})

        this.stopLoop()

        if(this.state.recording.mode === "RECORDING"){
            this.setRecording({
                mode: "STOP",
                active:"fiber_manual_record",
                color: "#c20000"
            })
        }
    }

    /**
     * handleStart
     *
     * This method starts the recording.
     * It clears the recorded notes dictionary, resets quanta and sets active. This is mainly used in PianoWithRecording component.
     */

    handleStart = () => {
        this.setState({recordedNotes:{}})
        this.setState({recording:{quanta:0, wait:true}})
        this.isActive = true
    }

    /**
     * setRecordedNotes
     *
     * This is a setter for the recorded notes state variable.
     *
     * @param val
     */

    setRecordedNotes = (val) =>{
        this.setState({recordedNotes:val})
    }

    /**
     * playLoop
     *
     * This method begins the metronome loop in the midisounds component.
     */

    playLoop(){
        console.log(this.state.recording.bpm)
        this.midiSounds.startPlayLoop([[[10],[]]], this.state.recording.bpm, 1/4, 1);
    }

    /**
     * stopLoop
     *
     * This method stops the metronome loop in the midisounds component.
     */

    stopLoop(){
        this.midiSounds.stopPlayLoop();
    }

    /**
     * processData
     *
     * This is the preprocessing method before the data is delivered to the track viewer.
     * It converts the recordedNotes dictionary into a data object inline with the JSON received from the server in
     * File Analysis.
     */

    processData = () =>{
        const tempData = []

        let quantaMap = {}
        for (let y = 0; y <= (this.state.config.noteRange.last - this.state.config.noteRange.first + 1); y++) {
            let noteBuffer = []
            let noteStart = null
            for (let x = 0; x < this.state.recording.length; x++) {
                if(this.state.recordedNotes[x+":"+y]) {
                    noteBuffer.push(x)
                    noteStart = (noteStart!==null)?noteStart:x
                }
                else if (noteStart !== null){
                    let tick = (4 * noteStart * this.state.recording.quantaLength) * this.state.ticksPerBeat
                    noteStart = null
                    let note = {
                        value: y + this.state.config.noteRange.first,
                        on_velocity: 100,
                        off_velocity: 0,
                        duration: (4 * noteBuffer.length * this.state.recording.quantaLength) * this.state.ticksPerBeat,
                        duration_beats: (4 * noteBuffer.length * this.state.recording.quantaLength),
                        isPercussive: false
                    }
                    if(!quantaMap[tick]){
                        quantaMap[tick] = [note]
                    }
                    else{
                        quantaMap[tick].push(note)
                    }
                    noteBuffer = []
                }
            }


        }
        let keys = Object.keys(quantaMap)
        keys.sort((item1,item2)=>{return (1.0*item1 - 1.0*item2)})
        for(let tick of keys){
            tempData.push({tick: tick , notes: quantaMap[tick]})
        }
        let dataElement = {data:{
                trackData:tempData,
                ticksPerBeat:this.state.ticksPerBeat,
                instrument:this.state.config.instrumentName
            }}
        this.setState(dataElement)
    }

    /**
     * This method returns the elements that we want displayed
     *
     * REQUIRED
     *
     * @returns {JSX.Element}
     */

    render() {

        /**
         * Audio context with a fallback.
         * webkitAudioContext fallback needed to support Safari.
         */
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();

        /**
         * Soundfont initializer.
         * @type {string}
         */
        const soundfontHostname = 'https://d1pzp51pvbm36p.cloudfront.net';

        /**
         * initialize the note range.
         * @type {{last: *, first: *}}
         */
        const noteRange = {
            first: MidiNumbers.fromNote('c3'),
            last: MidiNumbers.fromNote('f4'),
        }

        /**
         * Initialize keyboard shortcut for piano config.
         * @type {[]}
         */
        const keyboardShortcuts = KeyboardShortcuts.create({
            firstNote: noteRange.first,
            lastNote: noteRange.last,
            keyboardConfig: KeyboardShortcuts.HOME_ROW,
        })

        return (
            <div className="w-full overflow-auto">
                <div className="m-sm-30">
                    <div className="mb-sm-30">
                        <Breadcrumb
                            routeSegments={[
                                {name: "Live Analysis"}
                            ]}
                        />
                    </div>
                    <div>
                        <div style={{display:"none"}}>
                            <MIDISounds
                                ref={(ref) => (this.midiSounds = ref)}
                                appElementName="root"
                                drums={[10]}
                            />
                        </div>
                        <SimpleCard>
                            <div>
                                <h1 className="h3">Live Analysis</h1>
                                <asside className="text-muted">Here you can <b>Record</b> a sequence of notes or enter them manually
                                    in the <b>Piano Roll</b> to begin analysis. Press the green <b>Send to Viewer</b> button to process
                                    your sequence.</asside>
                                <div className="mt-5">
                                    <InstrumentListProvider
                                        hostname={soundfontHostname}
                                        render={(instrumentList) => (
                                            <div>
                                                <Grid container
                                                      justifyContent="flex-start"
                                                      direction="row"
                                                      alignItems="flex-start"
                                                >
                                                    <div className="m10 justify-end">
                                                        <Grid item>
                                                            <InstrumentMenu
                                                                setTrack={this.setInstrument}
                                                                inputOptions={instrumentList || [this.state.config.instrumentName]}
                                                            />
                                                        </Grid>
                                                        <br/>
                                                        <Grid item style={{
                                                            backgroundColor:"#F5F5F5",
                                                            boxShadow: "1px 2px #EAEAEAFF"
                                                        }}>
                                                            <Grid container
                                                                  justifyContent="flex-start"
                                                                  direction="row"
                                                                  alignItems="center"
                                                            >
                                                                <Grid item>
                                                                    <Tooltip title="Record" placement="top">
                                                                        <IconButton
                                                                            style={{color: this.state.recording.color}}
                                                                            aria-label="Record"
                                                                            onClick={this.onClickRecord}
                                                                        >
                                                                            <Icon>{this.state.recording.active}</Icon>
                                                                        </IconButton>
                                                                    </Tooltip>
                                                                </Grid>
                                                                <Grid item>
                                                                    {(this.state.recording.mode === "RECORDING")?
                                                                        <span>
                                                                            <LinearProgress style={{width:"50px"}} color="primary" />
                                                                        </span>
                                                                        :<span/>}
                                                                </Grid>
                                                                <Grid item>
                                                                    <Tooltip title="Clear" placement="top">
                                                                        <IconButton
                                                                            style={{color:(Object.keys(this.state.recordedNotes).length !== 0)?("#ffd300"):("#555555")}}
                                                                            aria-label="Clear"
                                                                            onClick={this.onClickClear}
                                                                        >
                                                                            <Icon>clear</Icon>
                                                                        </IconButton>
                                                                    </Tooltip>
                                                                </Grid>
                                                                <Grid item>
                                                                    <Tooltip title="Send To Viewer" placement="top">
                                                                        <IconButton
                                                                            style={{color:"#7cb518"}}
                                                                            aria-label="Process"
                                                                            onClick={this.processData}
                                                                        >
                                                                            <Icon>timeline</Icon>
                                                                        </IconButton>
                                                                    </Tooltip>
                                                                </Grid>
                                                                <Divider orientation="vertical" flexItem style={{marginInline:"20px"}}/>
                                                                <Grid item>
                                                                    <LiveSettings
                                                                        setRecording={this.setRecording}
                                                                        mode={this.state.recording.mode}
                                                                        BPM={this.state.recording.bpm}
                                                                        Length={this.state.recording.length}
                                                                        Wow={this.state.recording.quantaLength}
                                                                    />
                                                                </Grid>
                                                                <Divider orientation="vertical" flexItem style={{marginInline:"20px"}}/>
                                                                <Grid item>
                                                                    <p style={{width:"25px"}}/>
                                                                </Grid>
                                                                <Grid item>
                                                                    <PianoRoll state={this.state} setNotes={this.setRecordedNotes}/>
                                                                </Grid>
                                                                <Grid item>
                                                                    <p style={{width:"40px"}}/>
                                                                </Grid>
                                                            </Grid>
                                                        </Grid>
                                                        <br/>
                                                    </div>
                                                </Grid>
                                            </div>
                                        )}
                                    />
                                    <DimensionsProvider>
                                        {({ containerWidth }) => (
                                            <SoundfontProvider
                                                instrumentName={this.state.config.instrumentName}
                                                audioContext={audioContext}
                                                hostname={soundfontHostname}
                                                render={({ isLoading, playNote, stopNote}) => (
                                                    <PianoWithRecording
                                                        recordedNotes={this.state.recordedNotes}
                                                        recording={this.state.recording}
                                                        setRecording={this.setRecording}
                                                        noteRange={this.state.config.noteRange}
                                                        playNote={playNote}
                                                        stopNote={stopNote}
                                                        disabled={isLoading}
                                                        width={containerWidth}
                                                        keyboardShortcuts={keyboardShortcuts}
                                                        keyboardShortcutOffset={this.state.config.keyboardShortcutOffset}
                                                        length={this.state.recording.length}
                                                    />
                                                )}
                                            />
                                        )}
                                    </DimensionsProvider>
                                    <InstrumentListProvider
                                        hostname={soundfontHostname}
                                        render={(instrumentList) => (
                                            <div>
                                                <PianoConfig
                                                    config={this.state.config}
                                                    setConfig={(config) => {
                                                        this.setState({
                                                            config: Object.assign({}, this.state.config, config)
                                                        })
                                                    }}
                                                    keyboardShortcuts={keyboardShortcuts}
                                                />
                                            </div>
                                        )}
                                    />
                                </div>
                            </div>
                        </SimpleCard>
                    </div>
                    <br/>
                    <SimpleCard title="Timeline" subtitle="Here you'll find the analysis of your data. Make sure to enter and process it first.">
                        <TrackViewer trackData={this.state.data}/>
                        <br/>
                        <Grid
                            container
                            spacing={1}
                            direction="row"
                            justifyContent="flex-start"
                            alignItems="flex-start"
                        >
                            <div style={{marginLeft:"100px"}} id="dataDisplay"/>
                        </Grid>
                    </SimpleCard>
                </div>
            </div>
        );
    }
}

/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Live);
