import React from 'react';
import { Piano, KeyboardShortcuts, MidiNumbers } from 'react-piano';
import MdArrowDownward from '@material-ui/icons/ArrowDownward';

import DimensionsProvider from './DimensionsProvider';
import SoundfontProvider from './SoundfontProvider';
import {isMdScreen} from "../../utils";

class InteractiveDemo2 extends React.Component {
    state = {
        config: {
            instrumentName: 'acoustic_grand_piano',
            noteRange: {
                first: MidiNumbers.fromNote('c3'),
                last: MidiNumbers.fromNote('f5'),
            },
            keyboardShortcutOffset: 0,
        },
    };

    handleResize = () =>{
        if (isMdScreen()) {
            this.setState({
                config: {
                    noteRange: {
                        first: MidiNumbers.fromNote('e4'),
                        last: MidiNumbers.fromNote('f5'),
                    },
                },
            })
        }
        else{
            this.setState({
                config: {
                    noteRange: {
                        first: MidiNumbers.fromNote('c3'),
                        last: MidiNumbers.fromNote('f5'),
                    },
                },
            })
        }
    }

    componentDidMount() {
        window.addEventListener('resize', this.handleResize)
        this.handleResize()
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.handleResize);
    }

    render() {
        const keyboardShortcuts = KeyboardShortcuts.create({
            firstNote: this.state.config.noteRange.first + this.state.config.keyboardShortcutOffset,
            lastNote: this.state.config.noteRange.last + this.state.config.keyboardShortcutOffset,
            keyboardConfig: KeyboardShortcuts.HOME_ROW,
        });

        return (
            <SoundfontProvider
                audioContext={this.props.audioContext}
                instrumentName={this.state.config.instrumentName}
                hostname={this.props.soundfontHostname}
                render={({ isLoading, playNote, stopNote, stopAllNotes }) => (
                    <div>
                        <div className="text-center">
                            <p className="">Try it by clicking, tapping, or using your keyboard:</p>
                            <div style={{ color: '#ffffff' }}>
                                <MdArrowDownward size={32} />
                            </div>
                        </div>
                        <div className="mt-4">
                            <DimensionsProvider>
                                {({ containerWidth }) => (
                                    <Piano
                                        noteRange={this.state.config.noteRange}
                                        keyboardShortcuts={keyboardShortcuts}
                                        playNote={playNote}
                                        stopNote={stopNote}
                                        disabled={isLoading}
                                        width={containerWidth}
                                    />
                                )}
                            </DimensionsProvider>
                        </div>
                    </div>
                )}
            />
        );
    }
}

export default InteractiveDemo2;