import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Grid, Icon, IconButton} from "@material-ui/core";
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

/**
 * This class defines the interpretation of a midi file that has been supplied by the server
 * It displays:
 *      - Song Title
 *      - Piece Meta Data
 *          - Key
 *          - Time Signature
 *          - Tempo Indication
 *          - Genres
 *      - Tracks
 *      - Location In Track
 *      - Bar Information
 *          - Bar number
 *          - Chords
 *          - Bar events
 *          - Notes
 *              - Pitch
 *              - Velocity
 *              - Octave Value
 *
 * Navigation:
 *      -> Upload
 *
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

        this.timer = 0
        this.isActive = false
        this.isPaused = false
        this.interval = null

        this.state = {
            display: "",
            trackData:[],
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
                "#96BFFF"
            ],
            recording: {
                mode: 'STOP',
                events: [],
                currentEvents: [],
                active: "fiber_manual_record",
                color: "#c20000"
            },
            config: {
                instrumentName: 'accordion',
                noteRange: {
                    first: MidiNumbers.fromNote('c3'),
                    last: MidiNumbers.fromNote('f5'),
                },
                keyboardShortcutOffset: 0,
            }
        }
    }

    //====================================
    // DISPLAY STATE VALUE SETTERS
    //====================================

    /**
     * setTrackData
     * @param td
     */

    setTrackData = (td) => {

        this.setState({
            trackData: td
        })
    }

    /**
     * setTicksPerBeat
     * @param t
     */

    setTicksPerBeat = (t) => {
        this.setState({
            ticksPerBeat: t
        })
    }

    /**
     * setInstrument
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

    /**
     * getDigitsFromNumber
     * @param t
     * @returns {number[]|*[]}
     */

    getDigitsFromNumber = (t) => {
        if (typeof t !== 'number')
            return [0]

        t = ""+t
        let arrayDigits = []
        for(let char of t){
            arrayDigits.push(char)
        }
        return arrayDigits
    }



    //====================================
    // DISPLAY METHODS
    //====================================

    /**
     * getTrackMetadata
     *
     * Get the note data associated with recorded track
     * @param trackString
     * @param instrument
     * @param ticks_per_beat
     */
    getTrackMetadata = (trackString, instrument, ticks_per_beat) =>{

        trackString = JSON.parse(trackString)
        this.setTrackData(trackString)
        this.setTicksPerBeat(trackString[ticks_per_beat])
        this.setInstrument(trackString[instrument])

    }

    setRecording = value => {
        this.setState({
            recording: Object.assign({}, this.state.recording, value),
        });
    };

    onClickRecord = () => {
        const events = this.state.recording.events

        if(this.state.recording.mode === "STOP"){
            this.setRecording({
                mode: "RECORDING",
                events: [],
                active:"stop",
                color: "#555"
            })
            this.handleReset()
            this.handleStart()
        }
        else if(this.state.recording.mode === "RECORDING"){
            this.setState({
                recording:{
                    mode: "STOP",
                    events: events,
                    active:"fiber_manual_record",
                    color: "#c20000"
                }
            })
            this.handlePause()
        }
    }

    onClickPlay = () => {

    }

    incTimer = () => {
        if(this.isActive && !this.isPaused) {
            this.timer = this.timer + 1
            this.setState({display: this.formatTime(this.timer)})
        }
        else{
            clearInterval(this.interval)
        }
    }

    onClickClear = () => {
        this.handleReset()
        console.log(this.state.recording.mode)

        if(this.state.recording.mode === "RECORDING"){
            this.setRecording({
                mode: "STOP",
                events: [],
                active:"fiber_manual_record",
                color: "#c20000"
            })
        }
        else {
            this.setRecording({
                events: []
            })
        }
    }

    formatTime = (timer) => {
        const getSeconds = `0${(timer % 60)}`.slice(-2)
        const minutes = `${Math.floor(timer / 60)}`
        const getMinutes = `0${minutes % 60}`.slice(-2)

        return `${getMinutes} : ${getSeconds}`
    }

    handleStart = () => {
        this.isActive = true
        this.isPaused = false

        this.interval = setInterval(this.incTimer, 1000)

    }

    handlePause = () => {
        this.isPaused = true
    }

    handleReset = () => {
        this.isActive = false
        this.isPaused = false
        this.timer = 0
        this.setState({display: this.formatTime(this.timer)})
    }

    /**
     * This method returns the elements that we want displayed
     *
     * REQUIRED
     *
     * @returns {JSX.Element}
     */

    render() {

        // webkitAudioContext fallback needed to support Safari
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const soundfontHostname = 'https://d1pzp51pvbm36p.cloudfront.net';

        const noteRange = {
            first: MidiNumbers.fromNote('c3'),
            last: MidiNumbers.fromNote('f4'),
        }
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
                        <SimpleCard>
                            <div>
                            <h1 className="h3">Live Analysis</h1>
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
                                                    <Grid item style={{
                                                        backgroundColor:"#F5F5F5",
                                                        boxShadow: "1px 2px #EAEAEAFF"
                                                    }}>
                                                        <IconButton
                                                            style={{color: this.state.recording.color}}
                                                            aria-label="Record"
                                                            onClick={this.onClickRecord}
                                                        >
                                                            <Icon>{this.state.recording.active}</Icon>
                                                        </IconButton>
                                                        {this.state.display}
                                                        <IconButton
                                                            style={{color:"#38b000"}}
                                                            aria-label="Play"
                                                            onClick={this.onClickPlay}
                                                        >
                                                            <Icon>play_arrow</Icon>
                                                        </IconButton>
                                                        <IconButton
                                                            style={{color:"#ffba08"}}
                                                            aria-label="Clear"
                                                            onClick={this.onClickClear}
                                                        >
                                                            <Icon>clear</Icon>
                                                        </IconButton>
                                                        <IconButton
                                                            color="primary"
                                                            aria-label="Process"
                                                        >
                                                            <Icon>get_app</Icon>
                                                        </IconButton>
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
                                                    recording={this.state.recording}
                                                    setRecording={this.setRecording}
                                                    noteRange={this.state.config.noteRange}
                                                    width={containerWidth}
                                                    playNote={playNote}
                                                    stopNote={stopNote}
                                                    disabled={isLoading}
                                                    keyboardShortcuts={keyboardShortcuts}
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
                            <div className="mt-5">
                                <strong>Recorded notes</strong>
                                <div>{JSON.stringify(this.state.recording.events)}</div>
                            </div>
                        </div>
                            </SimpleCard>
                    </div>
                    <br/>
                    <SimpleCard title="Timeline" subtitle="Here you'll find the sequence of events for a chosen channel.">
                       <div>Title</div>
                        <TrackViewer trackData={{"trackData":this.state.trackData, "ticksPerBeat":this.state.ticksPerBeat, "instrument": this.state.instrument}} callSelect={this.setSelected}/>
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
