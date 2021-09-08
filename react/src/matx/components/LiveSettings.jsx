import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import {Icon, IconButton, Slider} from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
    },
}));

export default function DialogSelect(props) {
    const classes = useStyles();
    const [open, setOpen] = React.useState(false);
    const [BPM, setBPM] = React.useState(props.BPM);
    const [Length, setLength] = React.useState(props.Length);

    const handleClickOpen = () => {
        setOpen(true)
    }

    const handleClose = () => {
        setBPM(props.BPM)
        setLength(props.Length)
        setOpen(false)
    }
    const handleSubmit = () => {
        props.setRecording({bpm:BPM})
        props.setRecording({length:Length})
        props.update()
        setOpen(false)
    }

    const updateBPM = (event, value) => {
        setBPM(value)
    }

    const updateLength = (event, value) => {
        setLength(value)
    }

    return (
        <div>
            <IconButton
                onClick={handleClickOpen}
                color="primary"
                aria-label="Settings"
                disabled={props.mode !== "STOP"}
            >
                <Icon>settings</Icon>
            </IconButton>
            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Analysis Settings</DialogTitle>
                <DialogContent>
                    <br/>
                    <form className={classes.container}>
                        <FormControl className={classes.formControl}>
                            <InputLabel htmlFor="demo-dialog-native">BPM</InputLabel>
                            <Slider
                                style={{
                                    width: "200px"
                                }}
                                defaultValue={BPM}
                                aria-labelledby="discrete-slider-small-steps"
                                step={1}
                                min={40}
                                max={200}
                                valueLabelDisplay="auto"
                                onChangeCommitted={updateBPM}
                            />
                        </FormControl>
                        <FormControl className={classes.formControl}>
                            <InputLabel id="demo-dialog-select-label">Length</InputLabel>
                            <Slider
                                style={{
                                    width: "200px"
                                }}
                                defaultValue={Length}
                                aria-labelledby="discrete-slider-small-steps"
                                step={1}
                                min={10}
                                max={100}
                                valueLabelDisplay="auto"
                                onChangeCommitted={updateLength}
                            />
                        </FormControl>
                    </form>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleSubmit} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}