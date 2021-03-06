import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import {Scrollbar} from "react-scrollbars-custom";
import {Button, Grid, Icon, Tooltip} from "@material-ui/core";
import DialogActions from "@material-ui/core/DialogActions";
import React, {useEffect} from "react";

/**
 * PianoRoll
 *
 * The piano roll component used to record the sequence as well as providing a visual editor.
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */

function PianoRoll(props) {
    /**
     * state variables for dynamic rendering.
     */
    const [open, setOpen] = React.useState(false);
    const [xmax, setXMax] = React.useState(0);
    const [ymax, setYMax] = React.useState(0);

    /**
     * colorMap
     *
     * Color coding for individual notes.
     *
     * @type {string[]}
     */

    const colorMap = [
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
    ]

    /**
     * Flag for cell painting functionality
     */

    const [isDragging, setDragging] = React.useState(false);


    const range = (n) => {
        return [...Array(n).keys()]
    }

    /**
     * Styling for the component based on note and cursor change.
     *
     * @param active
     * @param y
     * @returns {{border: string, cursor: (string), backgroundColor: string, borderRadius: string, display: string, minWidth: string, height: string}}
     */

    const getStyle = (active,y) => {
        let color = colorMap[y % 12]
        let dragCursor = "grabbing"
        let defaultCursor = "pointer"
        if(active) return {height: "25px", minWidth: "25px",borderRadius: "0px",border: "0px", display: "inline", backgroundColor:color, cursor: (isDragging?dragCursor:defaultCursor)}
        return {height: "25px", minWidth: "25px", borderRadius: "0px", border: "1px solid #DEDEDEFF", display: "inline", backgroundColor:"white", cursor: (isDragging?dragCursor:defaultCursor)}
    }

    /**
     * adjustStyle
     *
     * Method tells a button to update it's style.
     *
     * @param target
     * @param targetStyle
     */

    const adjustStyle = (target, targetStyle) => {
        for(let attribute of Object.keys(targetStyle))
            target.style[attribute] = targetStyle[attribute]
    }

    /**
     * onButtonClick
     *
     * Handle a button on the piano roll being clicked.
     * Adds it to or removes it from the dictionary and adjusts it's style.
     *
     * @param x
     * @param y
     * @param event
     */

    const onButtonClick = (x,y,event) => {

        let index = x+":"+y
        let active = props.state.recordedNotes[index]===true

        if(active)
            delete props.state.recordedNotes[index]
        else props.state.recordedNotes[index] = true

        let targetStyle = getStyle(!active,y)
        adjustStyle(event.target,targetStyle)

    }

    /**
     * updateWH
     *
     * Updates the width and height in number of buttons.
     */

    const updateWH = () => {
        setXMax(props.state.recording.length)
        setYMax(props.state.config.noteRange.last - props.state.config.noteRange.first + 2)
    }

    /**
     * handleClickOpen
     *
     * Handles the opening of the piano roll
     */
    const handleClickOpen = () => {
        updateWH()
        setDragging(false)
        setOpen(true)
    }

    /**
     * handleClose
     *
     * Handles the closing of the piano roll
     */

    const handleClose = () => {
        setOpen(false)
    }

    /**
     * valueToNote
     *
     * Converts a midi value to it's simple name
     *
     * @param k
     * @returns {string}
     */

    const valueToNote = (k) =>{
        let noteArray = ["C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#/Bb","B"]
        let offset = k % 12
        let octave = Math.floor((k / 12) % 128) - 1
        let note = noteArray[offset]
        return (note+" "+octave)
    }

    /**
     * useEffect
     *
     * Shift key event listener for painting elements.
     */

    useEffect(()=> {
        window.addEventListener('keydown', (keyEvent) => {
            if (keyEvent.shiftKey && open) setDragging(!(isDragging))
        })
    })

    /**
     * keyboardKey
     *
     * The method for painting with the Shift toggle
     *
     * @param groupIndex
     * @param i
     * @returns {JSX.Element}
     */

    const keyboardKey = (groupIndex, i) => {
        let x = i, y = groupIndex - 1
        let id = x + ":" + y
        let active = props.state.recordedNotes[id] === true
        let style = getStyle(active, y)


        return (
            <button
                id={id}
                style={style}
                onClick={(event) => onButtonClick(x, y, event)}
                onMouseOver={(event) => {
                    if (isDragging)
                        onButtonClick(x, y, event)
                }}
            />
        )

    }

    /**
     * renderRow
     *
     * This method renders the buttons in in the rows of the piano roll
     *
     * @param groupIndex
     * @returns {JSX.Element|null}
     */

    const renderRow = (groupIndex) => {
        let noteValue = props.state.config.noteRange.first + (groupIndex - 1)
        let quanta = props.state.recording.quantaLength
        let blockStyle = {display: "inline", margin: "0px", width: "80px"}
        return ((groupIndex !== 0) ?
            (

                <div>
                    <div style={blockStyle}>
                        <Button id={groupIndex+"Label"} className="text-muted" style={{height: "30px", margin: "2px", width: "80px"}}
                                disabled={true}>
                            {valueToNote(noteValue)}
                        </Button>
                    </div>
                    {range(xmax).map((pad, index) => {
                            let beat = 1 + (4 * index * quanta)
                            let isDivider = Math.floor(beat) === beat
                            return (
                                <Tooltip id={"tool" + pad + index} title={valueToNote(noteValue) + " at beat " + beat}>
                                    <div id={"div" + pad + index} style={blockStyle}>
                                        {(isDivider && index) ? <div id={"space" + pad + index} style={blockStyle}>  </div> :
                                            <div id={"innerdiv" + pad + index} style={blockStyle}/>}
                                        {keyboardKey(groupIndex, index)}
                                    </div>
                                </Tooltip>
                            )
                        }
                    )}
                </div>

            )
            :
            null)
    }

    return (
        <div>
            <Button
                onClick={handleClickOpen}
                color="primary"
                aria-label="Settings"
                variant="outlined"
                disabled={(props.state.recording.mode === "RECORDING")}
            >
                <Icon>apps</Icon>
                <span>&nbsp;View Piano Roll</span>
            </Button>
            <Dialog
                fullWidth
                maxWidth="xl"
                style={{
                    whiteSpace: "nowrap",
                    position: 'absolute',
                }}
                open={open}
                onClose={handleClose}
            >
                <DialogTitle>Piano Roll</DialogTitle>
                <DialogContent style={{height: "600px"}}>
                    <Scrollbar>
                        <Grid container>
                            <Grid item>
                                {
                                    range(ymax).map((group, groupIndex) => {
                                        return renderRow(groupIndex)
                                    })
                                }
                            </Grid>
                        </Grid>
                    </Scrollbar>
                </DialogContent>
                <DialogActions>
                    <asside className="text-muted" ><b>Click</b> to add or remove notes to the sequence.
                        Press <b>Shift</b> to toggle paint mode, this will automatically paint notes as
                        your selection moves.</asside><span style={{width: "20px"}}/>
                    <Button onClick={handleClose} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}

/**
 * The final export of our view
 */

export default PianoRoll