import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import {Scrollbar} from "react-scrollbars-custom";
import {Button, Grid, Icon} from "@material-ui/core";
import DialogActions from "@material-ui/core/DialogActions";
import React, {useReducer} from "react";

function PianoRoll(props) {
    const [open, setOpen] = React.useState(false);
    const [h, setH] = React.useState([]);
    const [w, setW] = React.useState([]);
    const [notes, setNotes] = React.useState({});
    const [, forceUpdate] = useReducer(x => x + 1, 0);

    const onButtonClick = (x,y) => {
        const temp = notes

        if(temp[x+":"+y])
            delete temp[x+":"+y]
        else
            temp[x+":"+y] = true

        setNotes(temp)
        forceUpdate()
    }

    const updateWH = () => {
        const tempH = []
        const tempW = []

        for (let x = 0; x < props.state.recording.length; x++) {
            tempH.push(x)
        }

        for (let x = 0; x <= (props.state.config.noteRange.last - props.state.config.noteRange.first + 1); x++) {
            tempW.push(x)
        }

        setH(tempH)
        setW(tempW)
    }

    const handleClickOpen = () => {
        updateWH()
        loadNotes()
        setOpen(true)
    }

    const handleClose = () => {
        saveNotes()
        setOpen(false)
    }

    const valueToNote = (k) =>{
        let noteArray = ["C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#/Bb","B"]
        let offset = k % 12
        let octave = Math.floor((k / 12) % 128)
        let note = noteArray[offset]
        return (note+" "+octave)
    }

    const loadNotes = () => {
        setNotes(props.state.recordedNotes)
    }

    const saveNotes = () => {
        props.setNotes(notes)
    }

    return(
        <div>
            <Button
                onClick={handleClickOpen}
                color="primary"
                aria-label="Settings"
                variant="outlined"
                disabled={(props.state.recording.mode==="RECORDING")}
            >
                <Icon>apps</Icon>
                <span>&nbsp;View Piano Roll</span>
            </Button>
            <Dialog
                fullWidth
                maxWidth="xl"
                style={{
                    whiteSpace:"nowrap",
                    position: 'absolute',
                }}
                open={open}
                onClose={handleClose}
            >
                <DialogTitle>Piano Roll</DialogTitle>
                <DialogContent style={{height: "600px"}}>
                    <Scrollbar>
                        <Grid container >
                            <Grid item>
                            {
                                w.map((group, groupIndex) => {
                                    return(
                                        <div>
                                            {
                                                (groupIndex > 0)?
                                                    <Button className="text-muted" style={{height:"30px", margin:"2px", width:"80px"}} disabled={true}>
                                                        {valueToNote(groupIndex+props.state.config.noteRange.first-13)}
                                                    </Button>:<div style={{width:"80px", display:"inline-block"}}/>
                                            }
                                            {h.map((pad, i) => {
                                                return (
                                                    <div
                                                        style={{
                                                            display:"inline"
                                                        }}
                                                    >
                                                        {
                                                            (groupIndex > 0)?
                                                                <Button
                                                                    style={{height:"30px", minWidth:"30px", margin:"2px"}}
                                                                    variant={
                                                                        (notes[i+":"+groupIndex])?
                                                                            "contained"
                                                                            :"outlined"
                                                                    }
                                                                    key={`button-${i}`}
                                                                    onClick={()=>{onButtonClick(i,groupIndex)}}
                                                                    color={
                                                                        (notes[i+":"+groupIndex])?
                                                                            "secondary"
                                                                            :"default"
                                                                    }
                                                                />
                                                                : <Button
                                                                    size={"small"}
                                                                    className="text-muted"
                                                                    style={{height:"30px", minWidth:"32px", margin:"2px"}}
                                                                    disabled={true}
                                                                    key={`button-${i}`}>
                                                                    {i}
                                                                </Button>
                                                        }
                                                    </div>
                                                )
                                            })}
                                        </div>
                                    )}
                                )
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