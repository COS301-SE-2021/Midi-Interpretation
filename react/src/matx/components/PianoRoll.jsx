import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import {Scrollbar} from "react-scrollbars-custom";
import {Button, Grid, Icon, Tooltip} from "@material-ui/core";
import DialogActions from "@material-ui/core/DialogActions";
import React, {useEffect} from "react";

function PianoRoll(props) {
    const [open, setOpen] = React.useState(false);
    const [xmax, setXMax] = React.useState(0);
    const [ymax, setYMax] = React.useState(0);

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

    const [isDragging, setDragging] = React.useState(false);


    const range = (n) => {
        return [...Array(n).keys()]
    }

    const color = "#747474"

    const getStyle = (active,y) => {
        let color = colorMap[y % 12]
        let dragCursor = "grabbing"
        let defaultCursor = "pointer"
        if(active) return {height: "25px", minWidth: "25px",borderRadius: "0px",border: "0px", display: "inline", backgroundColor:color, cursor: (isDragging?dragCursor:defaultCursor)}
        return {height: "25px", minWidth: "25px", borderRadius: "0px", border: "1px solid #DEDEDEFF", display: "inline", backgroundColor:"white", cursor: (isDragging?dragCursor:defaultCursor)}
    }

    const adjustStyle = (target, targetStyle) => {
        for(let attribute of Object.keys(targetStyle))
            target.style[attribute] = targetStyle[attribute]
    }

    const onButtonClick = (x,y,event) => {

        let index = x+":"+y
        let active = props.state.recordedNotes[index]===true

        if(active)
            delete props.state.recordedNotes[index]
        else props.state.recordedNotes[index] = true

        let targetStyle = getStyle(!active,y)
        adjustStyle(event.target,targetStyle)

    }

    const updateWH = () => {
        setXMax(props.state.recording.length)
        setYMax(props.state.config.noteRange.last - props.state.config.noteRange.first + 1)
    }

    const handleClickOpen = () => {
        updateWH()
        setDragging(false)
        setOpen(true)
    }

    const handleClose = () => {
        setOpen(false)
    }

    const valueToNote = (k) =>{
        let noteArray = ["C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#/Bb","B"]
        let offset = k % 12
        let octave = Math.floor((k / 12) % 128)
        let note = noteArray[offset]
        return (note+" "+octave)
    }

    useEffect(()=> {
        window.addEventListener('keydown', (keyEvent) => {
            if (keyEvent.shiftKey) setDragging(!(isDragging))
        })
    })

    const keyboardKey = (groupIndex, i) => {
        let x = i, y = groupIndex - 1
        let id = x + ":" + y
        let active = props.state.recordedNotes[id] === true
        let style = getStyle(active, y)


        return (
            <button style={style}
                    onClick={(event) => onButtonClick(x, y, event)}
                    onMouseOver={(event) => {
                        if (isDragging)
                            onButtonClick(x, y, event)
                    }}
            />
        )

    }

    const renderRow = (groupIndex) => {
        let noteValue = props.state.config.noteRange.first + (groupIndex - 1)
        let quanta = props.state.recording.quantaLength
        let blockStyle = {display: "inline", margin: "0px", width: "80px"}
        return ((groupIndex !== 0) ?
            (

                <div>
                    <div style={blockStyle}>
                        <Button className="text-muted" style={{height: "30px", margin: "2px", width: "80px"}}
                                disabled={true}>
                            {valueToNote(noteValue)}
                        </Button>
                    </div>
                    {range(xmax).map((pad, index) => {
                            let beat = 1 + (4 * index * quanta)
                            let isDivider = Math.floor(beat) === beat
                            return (
                                <Tooltip title={valueToNote(noteValue) + " at beat " + beat}>
                                    <div style={blockStyle}>
                                        {(isDivider && index) ? <div style={blockStyle}>  </div> :
                                            <div style={blockStyle}/>}
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
                                        return renderRow(groupIndex, 4)
                                    })
                                }
                            </Grid>
                        </Grid>
                    </Scrollbar>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}

export default PianoRoll