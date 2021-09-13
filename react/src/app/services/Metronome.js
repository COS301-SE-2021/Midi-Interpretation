import React from 'react'
import PropTypes from 'prop-types'
import Slider from '@material-ui/core'
import metronomeWorker from './MetronomeWorker.js'
import '../../styles/components/_metronome.scss'

const faPlay = "PLaY"
const faPause = "pAuSE"
const faPlus = "pluS"
const faMinus = "miNUS"

class Metronome extends React.Component {
    constructor({startBpm=100, minBpm=40, maxBpm=200, volume=0.1, frequency=440.0, ...props}) {
        super()



        // instance variables
        this.minBpm = minBpm
        this.maxBpm = maxBpm
        this.volume = volume
        this.frequency = frequency
        this.nextNoteTime = 0.0
        this.secondsPerBeat = 60.0 / startBpm

        // custom style objects
        this.playPauseStyle = props.playPauseStyle ?? {}
        this.bpmStyle = props.bpmStyle ?? {}
        this.bpmTagStyle = props.bpmTagStyle ?? {}
        this.plusStyle = props.plusStyle ?? {}
        this.minusStyle = props.minusStyle ?? {}
        this.handleStyle = props.handleStyle ?? {}
        this.trackStyle = props.trackStyle ?? {}
        this.railStyle = props.railStyle ?? {}
        this.sliderStyle = props.sliderStyle ?? {}

        // initial state
        this.state = {
            bpm: startBpm,
            playing: false
        }

        // bind functions
        this.initAudio = this.initAudio.bind(this)
        this.handlePlayPause = this.handlePlayPause.bind(this)
        this.handleDecrement = this.handleDecrement.bind(this)
        this.handleIncrement = this.handleIncrement.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.tick = this.tick.bind(this)
    }

    componentDidMount() {
        // create a worker which runs an interval on a separate thread calling the tick function
        this.timerWorker = new Worker(window.URL.createObjectURL(new Blob([metronomeWorker])))

        // setup the listener for the tick message
        this.timerWorker.onmessage = ({ data }) => { data === "tick" && this.tick() }

        // set the interval on the worker
        this.timerWorker.postMessage({message: "interval", interval: 25.0})
    }

    componentWillUnmount() {
        // tell the worker to stop and close itself, rather than calling terminate from this scope
        this.timerWorker.postMessage({message: "close"})
    }

    // creates the audio context and buffer, sets the volume and starts playing
    initAudio() {
        // create the audio context
        this.audioContext = new AudioContext()

        this.volumeNode = this.audioContext.createGain()
        this.volumeNode.gain.value = this.volume
        this.volumeNode.connect(this.audioContext.destination)

        // create a buffer source and add the buffer
        var source = this.audioContext.createBufferSource()
        source.buffer = this.audioContext.createBuffer(1, 1, 22050)

        // start playing the audio data immediately
        source.start(0)
    }

    handlePlayPause() {
        // if the audio context hasn't been created, we need to set it up
        // we must create the audio context after a user gesture (browser autoplay policy)
        if (this.audioContext == null) {
            this.initAudio()
        }

        // start or stop the interval loop in the worker
        this.timerWorker.postMessage({message: !this.state.playing ? "start" : "stop" })

        // update the state so the play/pause icon re-renders
        this.setState((prevState) => ({
            ...prevState,
            playing: !prevState.playing
        }))
    }

    // event handlers for changing the BPM, clamps the value between MIN/MAX
    handleDecrement() { this.handleChange(this.state.bpm-1) }
    handleIncrement() { this.handleChange(this.state.bpm+1) }
    handleChange(newBpm) {
        if (newBpm < this.minBpm || newBpm > this.maxBpm) return
        this.setState((prevState) => ({
            ...prevState,
            bpm: newBpm
        }), ()=> {
            this.secondsPerBeat = 60.0 / this.state.bpm
        })
    }

    // fired when a tick message is received from the interval worker (only when playing)
    tick() {
        // when it is time to schedule a note to play
        // we use while becuase audioContext time is incrementing even when paused
        // so we loop until the nextNoteTime catches up
        while (this.nextNoteTime < this.audioContext.currentTime + 0.1 ) {

            // create an oscillator which generates a constant tone (a beep)
            var osc = this.audioContext.createOscillator()
            osc.connect( this.volumeNode )
            osc.frequency.value = this.frequency

            // start the beep at the next note time
            osc.start( this.nextNoteTime )

            // stop the beep after at the note length
            osc.stop( this.nextNoteTime + 0.075 )

            // calculate the time of the next note
            this.nextNoteTime += this.secondsPerBeat
        }
    }

    render() {
        return (
            <div className="metronome">
                <div className="metronome__top">
                    <div className="metronome__bpm">
                        <h1 style={this.bpmStyle}>{this.state.bpm}</h1>
                        <small style={this.bpmTagStyle}>BPM</small>
                    </div>
                    <div className="metronome__play-pause" onClick={this.handlePlayPause} style={this.playPauseStyle} >
                    </div>
                </div>
                <div className="metronome__slider" style={this.sliderStyle}>
                    <div className="slider__button slider__button--minus" onClick={this.handleDecrement} style={this.minusStyle}>
                    </div>
                    <Slider
                        min={this.minBpm}
                        max={this.maxBpm}
                        step={1}
                        value={this.state.bpm}
                        onChange={this.handleChange}
                        trackStyle={this.trackStyle}
                        handleStyle={this.handleStyle}
                        railStyle={this.railStyle} />
                    <div className="slider__button slider__button--plus" onClick={this.handleIncrement} style={this.plusStyle}>
                    </div>
                </div>
            </div>
        )
    }
}

Metronome.propTypes = {
    playPauseStyle: PropTypes.object,
    bpmStyle: PropTypes.object,
    bpmTagStyle: PropTypes.object,
    plusStyle: PropTypes.object,
    minusStyle: PropTypes.object,
    handleStyle: PropTypes.object,
    trackStyle: PropTypes.object,
    railStyle: PropTypes.object,
    sliderStyle: PropTypes.object,
    minBpm: PropTypes.number,
    maxBpm: PropTypes.number,
    startBpm: PropTypes.number,
    volume: PropTypes.number,
    frequency: PropTypes.number
}

export default Metronome