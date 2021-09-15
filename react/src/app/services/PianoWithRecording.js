import React from 'react';
import {Piano} from 'react-piano';

class PianoWithRecording extends React.Component {




    constructor(props) {
        super(props);
        this.playingNotes = {}
        this.noteStarts = {}
        this.totalNotes = []
    }

    onPlayNoteInput = (midiNumber) => {
        if(this.props.recording['mode'] === 'RECORDING' && !this.playingNotes[midiNumber]){
            this.playingNotes[midiNumber] = true
            const time = (new Date().getTime() - this.props.recording['initialTime'])/1000
            this.noteStarts[midiNumber] = this.quantaFromTime(time, this.props.recording.bpm, this.props.recording.quantaLength)
        }
    }

    onStopNoteInput = (midiNumber) => {

        if(this.props.recording['mode'] === 'RECORDING' && this.playingNotes[midiNumber]){
            this.playingNotes[midiNumber] = false
            const endTime = (new Date().getTime() - this.props.recording['initialTime'])/1000
            const startQuanta = this.noteStarts[midiNumber]
            let endQuanta = this.quantaFromTime(endTime,this.props.recording.bpm,this.props.recording.quantaLength)
            this.totalNotes.push({note:midiNumber, start: startQuanta, end: endQuanta})
            delete this.noteStarts[midiNumber]

            console.log(endTime)
            for(let n = startQuanta; n <= endQuanta; n++){
                this.props.recordedNotes[n+":"+(midiNumber-this.props.noteRange.first)] = true
            }
        }
    }

    quantaFromTime = (time,bpm,quantaLength)=>{
        return Math.floor(60*time/(bpm*quantaLength))
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
