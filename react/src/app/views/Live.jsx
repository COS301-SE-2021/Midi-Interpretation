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
import Metronome from "../services/Metronome"

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

    beep = () => {
        let snd = new Audio("data:audio/wav;base64,UklGRgxJAABXQVZFZm10IBAAAAABAAIARKwAABCxAgAEABAAZGF0YehIAAD1//T/AwADAPz////7//f//f////X/9P8MAAwA+v/8//D/7/8AAP//7//x//z/+/8DAAMA8v/y//n/+v/3//T/7//1/xcAEAD2//3/6//k/wUACgDn/+T/AAADAA4ADADz//T//P/7/+7/7/8AAAAAAQABAAMAAgDv//H/BQABAAUACgDt/+j/FwAdAP//+P/m/+z/CwAFAP3/BAALAAUA+/8AAAUAAQAEAAYA8//z/w8ADgADAAYA9//z//r//v/k/+D/BwAKAAsACQDa/93/BQABAPX/+f/y/+///f/9/+r/7f8HAAMA9v/5//z/+v8KAAsA/P/7/xcAGQAQAA4ADQANACkAKQAHAAYAKQAsACIAIAAIAAgAKgArADQAMQAhACUAEwAQABQAFwAcABgACQAOAAoABQAHAAoACwAKACcAKAAQAA4ABAAHADMALwALAA4ACwAKACUAJgAIAAUAFQAaACUAHgAdACQAKQAlAPT/9f8LAAsAAQAAAPX/9f8iACUA9P/v/+r/8P8GAAEA4f/k//H/8P8HAAYAFwAZAAEAAADp/+r/EgAQADAAMQAMAAwADQAMAC4AMQAaABYADQAQADYANAAeAB8AHAAdABsAGQAYABkA+f/5/+z/6/8PABEABgADAPn//f8HAAMA+f/8/+//7P/w//H/DAAOAAEA//8CAAMABgAGAP3//P8WABcA9P/0/wgABgAfACMA4v/e/+7/8f8FAAMA5//n//v//f8iAB8A0v/V//r/+P/x//L/7v/t/wAAAQDy//L/8v/y//j/+P/o/+j/CwAKAOH/5P/z/+//AgAGAOL/3v/0//j/8f/v/wUABQDh/+L/FgASAMr/0P/u/+n/7//z/yEAHgDC/8P/JgAlAMr/zP/0//H/yf/N/xAADADk/+b/DgAOAM7/zv/g/9//+v/8/9z/2v/U/9b/zP/L/y0ALACn/6n/DQALALb/uP9EAEQAZ/9k/54AogDQ/8z/owGnAeL84Pw0BDMEtfa39kb5RPl+FIAUVu9W7+oc6BwHAwkD2JHWkdRK1kqZDpcOs6y2rBtVF1XKvs6+EiwOLNFP1E/b2drZkWCRYK/BrcHc1uDWu2q3ajK+Nr7QksySOTw7PL11vXVPtk62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");
        snd.play().then(r => {})
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
                color: "#d00000",
                initialTime: null,
            })
        }
    }



    doTick = (time)=>{
        let threshold = this.state.recording.timerInterval
        let mpb = Math.floor(60000/this.state.recording.bpm)
        let nearest = Math.round(time/mpb)*mpb
        return ( Math.abs(time - nearest) <= 0.5 * threshold)
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

            const timeString = date.toISOString().substr(11, 5);
            this.setRecording({elapsed:timeString})
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
        this.interval = setInterval(this.incTimer, this.state.recording.timerInterval)
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
                        <SimpleCard title={"Metronome or something"}>
                            <Metronome />
                        </SimpleCard>
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
                                                                {/*<Grid item>*/}
                                                                {/*    <IconButton*/}
                                                                {/*        style={{color:"#38b000"}}*/}
                                                                {/*        aria-label="Play"*/}
                                                                {/*        onClick={this.onClickPlay}*/}
                                                                {/*    >*/}
                                                                {/*        <Icon>play_arrow</Icon>*/}
                                                                {/*    </IconButton>*/}
                                                                {/*</Grid>*/}
                                                                <Grid item>
                                                                    <IconButton
                                                                        style={{color:(Object.keys(this.state.recordedNotes).length !== 0)?("#ffd300"):("#555555")}}
                                                                        aria-label="Clear"
                                                                        onClick={this.onClickClear}
                                                                    >
                                                                        <Icon>clear</Icon>
                                                                    </IconButton>
                                                                </Grid>
                                                                <Grid item>
                                                                    <IconButton
                                                                        style={{color:"#7cb518"}}
                                                                        aria-label="Process"
                                                                        onClick={this.processData}
                                                                    >
                                                                        <Icon>timeline</Icon>
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
                                                                <Divider orientation="vertical" flexItem style={{marginInline:"20px"}}/>
                                                                <Grid item>
                                                                    <PianoRoll state={this.state} setNotes={this.setRecordedNotes}/>
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
                    </div>
                    <br/>
                    <SimpleCard title="Timeline" subtitle="Here you'll find the sequence of events for a chosen channel.">
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
