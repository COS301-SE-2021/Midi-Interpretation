import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Divider, Grid, Icon, IconButton} from "@material-ui/core";
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
                length:32,
                quanta:0,
                quantaLength:1/16,
                mode: 'STOP',
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
            },
            data:{
                trackData:[],
                ticksPerBeat:0,
                instrument:""
            },
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
        if(this.state.recording.mode === "STOP"){
            this.handleStart()
            this.setRecording({
                mode: "RECORDING",
                active:"stop",
                color: "#555",
                initialTime: new Date().getTime(),
            })
        }
        else if(this.state.recording.mode === "RECORDING"){
            this.isActive = false
            this.setRecording({
                mode: "STOP",
                active:"fiber_manual_record",
                color: "#c20000",
                initialTime: null,
            })
        }
    }

    onClickPlay = () => {

    }

    incTimer = () => {
        if(this.isActive) {
            if(this.state.recording.quanta >= this.state.recording.length){
                clearInterval(this.interval)
                this.setRecording({elapsed:0})
                this.onClickRecord()
            }
            let newElapsed =  new Date().getTime() - this.state.recording.initialTime
            let date = new Date(0)
            date.setSeconds(newElapsed);
            const timeString = date.toISOString().substr(11, 8);
            this.setRecording({elapsed:timeString})
            console.log(this.state.recordedNotes)
        }
        else{
            clearInterval(this.interval)
            this.setRecording({elapsed:0})
        }
    }

    onClickClear = () => {
        this.setRecording({elapsed:0, initialTime: null})

        this.setState({recordedNotes: {}})

        if(this.state.recording.mode === "RECORDING"){
            this.setRecording({
                mode: "STOP",
                active:"fiber_manual_record",
                color: "#c20000"
            })
        }
    }

    handleStart = () => {
        this.setState({recordedNotes:{}})
        this.setState({recording:{quanta:0, wait:true}})
        this.isActive = true
        this.interval = setInterval(this.incTimer, 100)
    }

    setRecordedNotes = (val) =>{
        this.setState({recordedNotes:val})
    }

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
                                                        <Grid container
                                                              justifyContent="flex-start"
                                                              direction="row"
                                                              alignItems="center"
                                                        >
                                                            <Grid item>
                                                                <IconButton
                                                                    style={{color: this.state.recording.color}}
                                                                    aria-label="Record"
                                                                    onClick={this.onClickRecord}
                                                                >
                                                                    <Icon>{this.state.recording.active}</Icon>
                                                                </IconButton>
                                                            </Grid>
                                                                <Grid item>
                                                                    {(this.state.recording.mode === "RECORDING")?
                                                                        <span>
                                                                            {this.state.recording.elapsed}
                                                                        </span>
                                                                        :<span/>}
                                                                </Grid>
                                                                <Grid item>
                                                                    <IconButton
                                                                        style={{color:"#38b000"}}
                                                                        aria-label="Play"
                                                                        onClick={this.onClickPlay}
                                                                    >
                                                                        <Icon>play_arrow</Icon>
                                                                    </IconButton>
                                                                </Grid>
                                                                <Grid item>
                                                                    <IconButton
                                                                        style={{color:(Object.keys(this.state.recordedNotes).length !== 0)?("#ffba08"):("#555555")}}
                                                                        aria-label="Clear"
                                                                        onClick={this.onClickClear}
                                                                    >
                                                                        <Icon>clear</Icon>
                                                                    </IconButton>
                                                                </Grid>
                                                                <Grid item>
                                                                    <IconButton
                                                                        color="primary"
                                                                        aria-label="Process"
                                                                        onClick={this.processData}
                                                                    >
                                                                        <Icon>get_app</Icon>
                                                                    </IconButton>
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
                                                                <Grid item>
                                                                    <p style={{width:"20px"}}/>
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
                                                    width={containerWidth}
                                                    playNote={playNote}
                                                    stopNote={stopNote}
                                                    disabled={isLoading}
                                                    keyboardShortcuts={keyboardShortcuts}
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
                        <br/>
                    <SimpleCard title="Piano Roll">
                        <div>
                            <PianoRoll state={this.state} setNotes={this.setRecordedNotes}/>
                        </div>
                    </SimpleCard>
                    </div>
                    <br/>
                    <SimpleCard title="Timeline" subtitle="Here you'll find the sequence of events for a chosen channel.">
                       <div>Title</div>
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
