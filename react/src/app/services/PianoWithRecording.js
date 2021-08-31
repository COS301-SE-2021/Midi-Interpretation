import React from 'react';
import { Piano } from 'react-piano';

class PianoWithRecording extends React.Component {
    static defaultProps = {
        notesRecorded: false,
    }
    keysDown = {}
    allNotes = []
    state = {
        start: 0,
    }

    onPlayNoteInput = midiNumber => {
        if (this.props.recording.mode !== 'RECORDING') {
            return
        }

        if(Object.keys(this.keysDown).length === 0 && this.props.recording.events.length === 0){
            this.setState({start: Date.now()})
        }

        if(!this.keysDown.hasOwnProperty(midiNumber)) {
            this.keysDown[midiNumber] = {value: midiNumber, time: Date.now(), duration:0}
            console.log("playing " + midiNumber)
        }
    }

    onStopNoteInput = (midiNumber) => {
        if (this.props.recording.mode !== 'RECORDING') {
            return
        }

        if(this.keysDown[midiNumber] !== undefined) {

            this.keysDown[midiNumber] = {
                value: midiNumber,
                time: this.getTime(this.keysDown[midiNumber].time, this.state.start, 1),
                duration: this.getTime(Date.now(), this.keysDown[midiNumber].time, 2)
            }

            this.allNotes.push(this.keysDown[midiNumber])
            delete this.keysDown[midiNumber]

            this.props.setRecording({
                events: this.allNotes
            })

            console.log(this.allNotes)
        }
    }

    getTime = (start, stop, precision) => {
        return ((start - stop)/1000).toFixed(precision)
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
