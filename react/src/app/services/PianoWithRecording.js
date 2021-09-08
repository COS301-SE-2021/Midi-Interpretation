import React from 'react';
import { Piano } from 'react-piano';

class PianoWithRecording extends React.Component {

    constructor(props) {
        super(props);
        this.playingNote = {}
    }

    onPlayNoteInput = midiNumber => {
        if (this.props.recording.mode === 'RECORDING') {
            if(Object.keys(this.props.recordedNotes).length === 0)
                this.props.setRecording({wait:false})

            this.playingNote[midiNumber] = this.props.recording.quanta
        }

    }

    onStopNoteInput = (midiNumber) => {
        if (this.props.recording.mode === 'RECORDING' && Object.keys(this.playingNote).length !== 0) {
            const start = this.playingNote[midiNumber]
            const end = this.props.recording.quanta

            console.log(this.playingNote)

            for(let n = start; n <= end; n++){
                this.props.recordedNotes[(midiNumber-this.props.noteRange.first)+":"+n] = {
                    enabled: true,
                    variant: "contained",
                    colour: "secondary"
                }
            }

            delete this.playingNote[midiNumber]
        }
    }

    render() {
        const {
            playNote,
            stopNote,
            recording,
            setRecording,
            ...pianoProps
        } = this.props;
        return (
            <div>
                    <Piano
                        playNote={this.props.playNote}
                        stopNote={this.props.stopNote}
                        onPlayNoteInput={this.onPlayNoteInput}
                        onStopNoteInput={this.onStopNoteInput}
                        {...pianoProps}
                    />
            </div>
        )
    }
}

export default PianoWithRecording;
