import React from 'react';
import { Piano, KeyboardShortcuts, MidiNumbers } from 'react-piano';
import DimensionsProvider from './DimensionsProvider';
import SoundfontProvider from './SoundfontProvider';
import {isMdScreen} from "../../utils";

class InteractiveDemo extends React.Component {
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
          instrumentName: 'acoustic_grand_piano',
          noteRange: {
            first: MidiNumbers.fromNote('e4'),
            last: MidiNumbers.fromNote('f5'),
          },
          keyboardShortcutOffset: 0,
        },
      })
    }
    else{
      this.setState({
        config: {
          instrumentName: 'acoustic_grand_piano',
          noteRange: {
            first: MidiNumbers.fromNote('c3'),
            last: MidiNumbers.fromNote('f5'),
          },
          keyboardShortcutOffset: 0,
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
          render={({ isLoading, playNote, stopNote}) => (
              <div>
                <div className="mt-2">
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
export default InteractiveDemo;