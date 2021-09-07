import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Button, Divider, Grid, Icon, IconButton} from "@material-ui/core";
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
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import { Scrollbar } from "react-scrollbars-custom";

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
                bpm:120,
                length:40,
                quanta:0,
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
            h: [],
            w: [],
        }

        for (let x = 0; x < (this.state.config.noteRange.last - this.state.config.noteRange.first)+2; x++) {
            this.state.h.push(x)
        }

        for (let x = 0; x < this.state.recording.length; x++) {
            this.state.w.push(x)
        }
    }

    //====================================
    // DISPLAY STATE VALUE SETTERS
    //====================================

    setOpen = (o) => {
        this.setState({open:o})
    }

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
                color: "#555"
            })
        }
        else if(this.state.recording.mode === "RECORDING"){
            this.isActive = false
            this.setRecording({
                mode: "STOP",
                active:"fiber_manual_record",
                color: "#c20000"
            })
        }
    }

    onClickPlay = () => {

    }

    onButtonClick = (x,y) => {
        console.log(x+":"+y)
        console.log(this.state.recordedNotes)
        this.state.recordedNotes[x+":"+y] = true
    }

    incTimer = () => {
        if(this.isActive) {
            this.setRecording({quanta: this.state.recording.quanta + 1})
        }
        else{
            clearInterval(this.interval)
            this.setRecording({quanta:0})
        }
    }

    onClickClear = () => {
        this.setRecording({quanta:0})

        this.setState({recordedNotes: {}})

        if(this.state.recording.mode === "RECORDING"){
            this.setRecording({
                mode: "STOP",
                active:"fiber_manual_record",
                color: "#c20000"
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
        this.setState({recordedNotes:{}})
        this.setState({recording:{quanta:0}})
        this.isActive = true
        this.interval = setInterval(this.incTimer, (this.state.recording.bpm*1000)/960)
    }

    valueToNote = (k) =>{
        let noteArray = ["C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#/Bb","B"]
        let offset = k % 12
        let octave = Math.floor((k / 12) % 128)
        let note = noteArray[offset]
        return (note+" "+octave)
    }

    handleClickOpen = () => {
        this.setOpen(true)
    }

    handleClose = () => {
        this.setOpen(false)
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
                                                                        <span>Beat: {this.state.recording.quanta/16}</span>
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
                                                                        style={{color:"#ffba08"}}
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
                            <Button
                                onClick={this.handleClickOpen}
                                color="primary"
                                aria-label="Settings"
                                variant="outlined"
                            >
                                <Icon>apps</Icon>
                                <span>&nbsp;View Piano Roll</span>
                            </Button>
                            <Dialog
                                fullWidth
                                maxWidth="xl"
                                maxHeight="xl"
                                style={{
                                    whiteSpace:"nowrap",
                                    position: 'absolute',
                                }}
                                open={this.state.open}
                                onClose={this.handleClose}
                            >
                                <DialogTitle>Piano Roll</DialogTitle>
                                    <DialogContent style={{height: "800px"}}>
                                        <Scrollbar>
                                        <div>
                                            {
                                                this.state.w.map((group, groupIndex) => {
                                                    return(
                                                        <div>
                                                            {
                                                                (groupIndex > 0)?
                                                                    <Button className="text-muted" style={{height:"30px", margin:"2px", width:"80px"}} disabled="true">
                                                                    {this.valueToNote(this.state.config.noteRange.first+groupIndex)}
                                                                    </Button>:<div style={{width:"80px", display:"inline-block"}}/>
                                                            }
                                                            {this.state.h.map((pad, i) => {
                                                                return (
                                                                    <div
                                                                        style={{
                                                                            display:"inline"
                                                                        }}
                                                                    >
                                                                        {
                                                                            (groupIndex > 0)?
                                                                            <Button
                                                                                style={{height:"30px", margin:"2px"}}
                                                                                variant={(Boolean(this.state.recordedNotes[groupIndex+":"+i]))?"contained":"outlined"}
                                                                                key={`button-${i}`}
                                                                                onClick={()=>{this.onButtonClick(groupIndex,i)}}
                                                                                color={(Boolean(this.state.recordedNotes[groupIndex+":"+i]))?"secondary":"default"}
                                                                            />
                                                                                : <Button className="text-muted" style={{height:"30px", margin:"2px"}} disabled="true" key={`button-${i}`}>{i}</Button>
                                                                        }
                                                                    </div>
                                                                )
                                                            })}
                                                        </div>
                                                    )}
                                                )
                                            }
                                        </div>
                                        </Scrollbar>
                                    </DialogContent>
                                <DialogActions>
                                    <Button onClick={this.handleClose} color="primary">
                                        Ok
                                    </Button>
                                </DialogActions>
                            </Dialog>
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
