import React from 'react';
import { MidiNumbers } from 'react-piano';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import {Grid} from "@material-ui/core";

class PianoConfig extends React.Component {
  constructor(props) {
    super(props)

    this.state ={
      anchorElFirst: null,
      anchorElLast: null,
      selectedFirstIndex: this.props.config.noteRange.first,
      selectedLastIndex: this.props.config.noteRange.last
    }

    this.setAnchorElFirst = (af) => {
      this.setState({anchorElFirst: af})
    }
    this.setAnchorElLast = (al) => {
      this.setState({anchorElLast: al})
    }
    this.setSelectedFirstIndex = (f) => {
      this.setState({selectedFirstIndex: f})
    }
    this.setSelectedLastIndex = (l) => {
      this.setState({selectedLastIndex: l})
    }

  }

  /**
   * Handle the list being clicked
   * @param event - The click event
   */

  handleClickListItemFirst = (event) => {
    this.setAnchorElFirst(event.currentTarget);
  }

  /**
   * Select an item on the menu, change the index of the current item and change the displayed item
   * @param event - The click event
   * @param index - the index of the selected menu item
   * @param newIndex
   */
  handleMenuItemClickFirst = (event, index, newIndex) => {
    this.setSelectedFirstIndex(newIndex)
    this.onChangeFirstNote(event, index)
    this.setAnchorElFirst(null)
  }

  /**
   * Close the menu
   */

  handleCloseFirst = () => {
    this.setAnchorElFirst(null);
    this.props.setConfig({})
  }

  /**
   * Handle the list being clicked
   * @param event - The click event
   */

  handleClickListItemLast = (event) => {
    this.setAnchorElLast(event.currentTarget);
  }

  /**
   * Select an item on the menu, change the index of the current item and change the displayed item
   * @param event - The click event
   * @param index - the index of the selected menu item
   * @param newIndex
   */

  handleMenuItemClickLast = (event, index, newIndex) => {
    this.setSelectedLastIndex(newIndex);
    this.onChangeLastNote(event, index)
    this.setAnchorElLast(null);
  }

  /**
   * Close the menu
   */

  handleCloseLast = () => {
    this.setAnchorElLast(null);
  }

  componentDidMount = () => {
    window.addEventListener('keydown', this.handleKeyDown);
  }

  componentWillUnmount = () => {
    window.removeEventListener('keydown', this.handleKeyDown);
  }

  handleKeyDown = (event) => {

    const numNotes = this.props.config.noteRange.last - this.props.config.noteRange.first + 1;
    const minOffset = 0;
    const maxOffset = numNotes - this.props.keyboardShortcuts.length;
    if (event.key === 'ArrowLeft') {
      const reducedOffset = this.props.config.keyboardShortcutOffset - 1;
      if (reducedOffset >= minOffset) {
        this.props.setConfig({
          keyboardShortcutOffset: reducedOffset,
        });
      }
    } else if (event.key === 'ArrowRight') {
      const increasedOffset = this.props.config.keyboardShortcutOffset + 1;
      if (increasedOffset <= maxOffset) {
        this.props.setConfig({
          keyboardShortcutOffset: increasedOffset,
        });
      }
    }
  };

  onChangeFirstNote = (midiNumber, index) => {
    this.props.setConfig({
      noteRange: {
        first: parseInt(MidiNumbers.NATURAL_MIDI_NUMBERS[index],10),
        last: this.props.config.noteRange.last,
      },
    });
  };

  onChangeLastNote = (midiNumber, index) => {
    this.props.setConfig({
      noteRange: {
        first: this.props.config.noteRange.first,
        last: MidiNumbers.NATURAL_MIDI_NUMBERS[index],
      },
    });
  };

  render() {
    const midiNumbersToNotes = MidiNumbers.NATURAL_MIDI_NUMBERS.reduce((obj, midiNumber) => {
      obj[midiNumber] = MidiNumbers.getAttributes(midiNumber).note
      return obj
    }, {})

    let counter = 0
    const midiNumbersToIndex = MidiNumbers.NATURAL_MIDI_NUMBERS.reduce((obj, midiNumber) => {
      obj[midiNumber] = counter
      counter++
      return obj
    }, {})
    const { noteRange } = this.props.config;
    return (
      <div>
        <div>
          <Grid container direction="row" justifyContent="space-between">
            <Grid item>
              <List component="nav" aria-label="Device settings">
                <ListItem
                    button
                    aria-haspopup="true"
                    aria-controls="first-note-menu"
                    aria-label="First"
                    onClick={this.handleClickListItemFirst}
                >
                  <ListItemText
                      primary="First Note"
                      secondary={midiNumbersToNotes[noteRange.first]}
                  />
                </ListItem>
              </List>
              <Menu
                  id="lock-menu"
                  anchorEl={this.state.anchorElFirst}
                  keepMounted
                  open={Boolean(this.state.anchorElFirst)}
                  onClose={this.handleCloseFirst}
              >
                {MidiNumbers.NATURAL_MIDI_NUMBERS.slice(midiNumbersToIndex[0],midiNumbersToIndex[noteRange.last]).map((midiNumber, index) => (
                    <MenuItem
                        key={midiNumber}
                        selected={index === midiNumbersToIndex[this.state.selectedFirstIndex]}
                        onClick={midiNumber =>this.handleMenuItemClickFirst(midiNumber, index, MidiNumbers.NATURAL_MIDI_NUMBERS[index])}
                    >
                      <option value={midiNumber} disabled={midiNumber >= noteRange.last} key={midiNumber}>
                        {midiNumbersToNotes[midiNumber]}
                      </option>
                    </MenuItem>
                ))}
              </Menu>
            </Grid>
            <Grid item>
              <List component="nav" aria-label="Device settings">
                <ListItem
                    button
                    aria-haspopup="true"
                    aria-controls="last-note-menu"
                    aria-label="Last"
                    onClick={this.handleClickListItemLast}
                >
                  <ListItemText
                      primary="Last Note"
                      secondary={midiNumbersToNotes[noteRange.last]}
                  />
                </ListItem>
              </List>
              <Menu
                  id="lock-menu"
                  anchorEl={this.state.anchorElLast}
                  keepMounted
                  open={Boolean(this.state.anchorElLast)}
                  onClose={this.handleCloseLast}
              >
                {MidiNumbers.NATURAL_MIDI_NUMBERS.slice(midiNumbersToIndex[noteRange.first]+1, midiNumbersToIndex[127]).map((midiNumber, index) => (
                    <MenuItem
                        key={midiNumber}
                        selected={index+midiNumbersToIndex[noteRange.first]+1 === midiNumbersToIndex[this.state.selectedLastIndex]}
                        onClick={midiNumber =>this.handleMenuItemClickLast(midiNumber,
                            index+midiNumbersToIndex[noteRange.first]+1,
                            MidiNumbers.NATURAL_MIDI_NUMBERS[index+midiNumbersToIndex[noteRange.first]+1])}
                    >
                          <option value={midiNumber} disabled={midiNumber <= noteRange.first} key={midiNumber}>
                            {midiNumbersToNotes[midiNumber]}
                          </option>
                    </MenuItem>
                ))}
              </Menu>
            </Grid>
          </Grid>
        </div>
      </div>
    )
  }
}

export default PianoConfig;
