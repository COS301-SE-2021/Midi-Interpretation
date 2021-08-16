import React from "react";
import "../../app/services/styles.css"
import Modal from '@material-ui/core/Modal';
import CircularProgress from "@material-ui/core/CircularProgress";
import InteractiveDemo from "../../app/services/InteractiveDemo";
import {Button, Grid} from "@material-ui/core";
import MdArrowDownward from "@material-ui/icons/ArrowDownward";


const styles = {
    modal: {
        backgroundColor: "transparent",
        boxShadow: "none",
        display: "flex",
        overflow: "none",
        width: "100%",
        padding: "0",
        margin: "0",
        height: "100%",
        minWidth: "100%",
        justifyContent: "center"
    },
    overlay: {
        backgroundColor: "#1cccc",
        padding: 0
    },
    closeIcon: {
        fill: "#fff"
    }
};

function getModalStyle() {
    const top = 50
    const left = 50

    return {
        top: 0,
        left: 0,
    };
}

function getUseStyles (){
    return {
        position: 'absolute',
        width: "100%",
        height: "100%",
        backgroundColor: "white",
    }
}

function Load (props) {

    const [modalStyle] = React.useState(getModalStyle);
    const [useStyles] = React.useState(getUseStyles);
    const modalVisible = props.modalVisible
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const soundfontHostname = 'https://d1pzp51pvbm36p.cloudfront.net';

    return (
        <div>
            <Modal
                open={modalVisible}
            >
                <div style={modalStyle}>
                    <div style={useStyles}>
                        <Grid container
                              spacing={5}
                              direction="column"
                              justifyContent="space-between"
                              alignItems="center">
                            <Grid item>
                                <br/>
                            </Grid>
                            <Grid item>
                            <CircularProgress />
                            </Grid>
                            <Grid item>
                            <h1>Loading...</h1>
                            </Grid>
                            <Grid item>
                            <div className="w-full-screen ">
                                <div className="text-center">
                                    <h4>We are processing your file.</h4>
                                    <p className="text-16">This may take some time. In the meantime, try it by clicking, tapping, or using your keyboard:</p>
                                    <div style={{ color: '#777' }}>
                                        <MdArrowDownward size={32} />
                                    </div>
                                </div>
                                <InteractiveDemo audioContext={audioContext} soundfontHostname={soundfontHostname} />
                            </div>
                            </Grid>
                            <Grid item>
                            <Button
                                className="capitalize"
                                variant="contained"
                                color="primary"
                                onClick={() => window.location.reload()}
                            >
                                Back to Upload
                            </Button>
                            </Grid>
                        </Grid>
                    </div>
                </div>
            </Modal>
        </div>
    )
}

export default Load;