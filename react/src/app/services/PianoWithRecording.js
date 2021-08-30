import React from 'react';
import { Piano } from 'react-piano';

class PianoWithRecording extends React.Component {
    static defaultProps = {
        notesRecorded: false,
    }

    state = {
        lastDownTime: Date.now(),
        lastUpTime: Date.now(),
        currentTime: 0,
        keysDown: {},
    }

    onPlayNoteInput = midiNumber => {
        this.setState({
            notesRecorded: false,
            lastDownTime: Date.now(),
            currentTime: this.state.currentTime+this.getTime(Date.now()-this.state.lastUpTime)
        })
    }

    onStopNoteInput = (midiNumber, { prevActiveNotes }) => {
        if (this.state.notesRecorded === false) {
            this.recordNotes(prevActiveNotes, this.getTime(Date.now(),this.state.lastDownTime))
            this.setState({
                notesRecorded: true,
                lastUpTime: Date.now()
            })
        }
    }

    recordNotes = (midiNumbers, duration) => {
        if (this.props.recording.mode !== 'RECORDING') {
            return
        }
        const newEvents = midiNumbers.map(midiNumber => {
            return {
                midiNumber,
                time: this.props.recording.currentTime,
                duration: duration,
            }
        })
        this.setState({currentTime: this.state.currentTime + duration})
        this.props.setRecording({
            events: this.props.recording.events.concat(newEvents),
            currentTime: this.props.recording.currentTime + duration,
        })
    }

    getTime = (start, stop) => {
        return (start - stop)/1000
    }

    render() {
        const {
            playNote,
            stopNote,
            recording,
            setRecording,
            ...pianoProps
        } = this.props;

        const { mode, currentEvents } = this.props.recording;
        const activeNotes =
            mode === 'PLAYING' ? currentEvents.map(event => event.midiNumber) : null;
        return (
            <div>
                    <Piano
                        playNote={this.props.playNote}
                        stopNote={this.props.stopNote}
                        onPlayNoteInput={this.onPlayNoteInput}
                        onStopNoteInput={this.onStopNoteInput}
                        activeNotes={activeNotes}
                        {...pianoProps}
                    />
            </div>
        );
    }
}

export default PianoWithRecording;
