import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "../../matx";
import MidiSenseService from "../services/MidiSenseService";
import TrackViewer from "../../matx/components/TrackViewer";
import {Grid} from "@material-ui/core";
import Cookies from "universal-cookie";
import {withStyles} from "@material-ui/core/styles";
import _ from 'lodash';
import { KeyboardShortcuts, MidiNumbers } from 'react-piano';
import 'react-piano/dist/styles.css';
import SoundfontProvider from '../services/SoundfontProvider';
import PianoWithRecording from '../services/PianoWithRecording';
import InstrumentListProvider from "../services/InstrumentListProvider";
import PianoConfig from "../services/PianoConfig";
import DimensionsProvider from "../services/DimensionsProvider";

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

        this.state = {
            trackData:[],
            ticksPerBeat:1,
            instrument: "Unknown",
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
                mode: 'RECORDING',
                events: [],
                currentTime: 0,
                currentEvents: [],
            },
            config: {
                instrumentName: 'acoustic_grand_piano',
                noteRange: {
                    first: MidiNumbers.fromNote('c3'),
                    last: MidiNumbers.fromNote('f5'),
                },
                keyboardShortcutOffset: 0,
            }
        }

        this.scheduledEvents = []
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
            instrument: i
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

    getRecordingEndTime = () => {
        if (this.state.recording.events.length === 0) {
            return 0;
        }
        return Math.max(
            ...this.state.recording.events.map(event => event.time + event.duration),
        );
    };

    setRecording = value => {
        this.setState({
            recording: Object.assign({}, this.state.recording, value),
        });
    };

    onClickPlay = () => {
        this.setRecording({
            mode: 'PLAYING',
        });
        const startAndEndTimes = _.uniq(
            _.flatMap(this.state.recording.events, event => [
                event.time,
                event.time + event.duration,
            ]),
        );
        startAndEndTimes.forEach(time => {
            this.scheduledEvents.push(
                setTimeout(() => {
                    const currentEvents = this.state.recording.events.filter(event => {
                        return event.time <= time && event.time + event.duration > time;
                    });
                    this.setRecording({
                        currentEvents,
                    });
                }, time * 1000),
            );
        });
        // Stop at the end
        setTimeout(() => {
            this.onClickStop();
        }, this.getRecordingEndTime() * 1000);
    };

    onClickStop = () => {
        this.scheduledEvents.forEach(scheduledEvent => {
            clearTimeout(scheduledEvent);
        });
        this.setRecording({
            mode: 'RECORDING',
            currentEvents: [],
        });
    };

    onClickClear = () => {
        this.onClickStop();
        this.setRecording({
            events: [],
            mode: 'RECORDING',
            currentEvents: [],
            currentTime: 0,
        });
    };

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
        };
        const keyboardShortcuts = KeyboardShortcuts.create({
            firstNote: noteRange.first,
            lastNote: noteRange.last,
            keyboardConfig: KeyboardShortcuts.HOME_ROW,
        });

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
                            <h1 className="h3">react-piano recording + playback demo</h1>
                            <div className="mt-5">
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
                                        <PianoConfig
                                            config={this.state.config}
                                            setConfig={(config) => {
                                                this.setState({
                                                    config: Object.assign({}, this.state.config, config),
                                                });
                                                //stopAllNotes();
                                            }}
                                            instrumentList={instrumentList || [this.state.config.instrumentName]}
                                            keyboardShortcuts={keyboardShortcuts}
                                        />
                                    )}
                                />
                            </div>
                            <div className="mt-5">
                                <button onClick={this.onClickPlay}>Play</button>
                                <button onClick={this.onClickStop}>Stop</button>
                                <button onClick={this.onClickClear}>Clear</button>
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
