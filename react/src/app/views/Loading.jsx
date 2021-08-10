import React, { Component } from "react";
import { withStyles } from "@material-ui/styles";
import CircularProgress from "@material-ui/core/CircularProgress";
import "../services/styles.css"
import InteractiveDemo from "../services/InteractiveDemo";
import {Button} from "@material-ui/core";

/**
 * Styling for the page
 * @param theme
 * @returns {{flexCenter: {alignItems: string, display: string, justifyContent: string}, wrapper: {width: string, height: string}, inner: {flexDirection: string, maxWidth: string}}}
 */

const styles = theme => ({
    flexCenter: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center"
    },
    wrapper: {
        width: "100%",
        height: "100vh"
    },
    inner: {
        flexDirection: "column",
        maxWidth: "80%"
    }
});

/**
 * The 404 page for the application
 */

class Loading extends Component {
    state = {};

    render() {
        const { classes } = this.props;

        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const soundfontHostname = 'https://d1pzp51pvbm36p.cloudfront.net';

        return (
            <div className={`${classes.flexCenter} ${classes.wrapper}`}>
                <div className={`${classes.flexCenter} ${classes.inner}`}>
                    <CircularProgress />
                    <br/>
                    <h1>Loading...</h1>
                    <br/>
                    <br/>
                    <div className="w-full-screen ">
                        <InteractiveDemo audioContext={audioContext} soundfontHostname={soundfontHostname} />
                    </div>
                    <br/>
                    <Button
                        className="capitalize"
                        variant="contained"
                        color="primary"
                        onClick={() => this.props.history.push("/")}
                    >
                        Back to Home
                    </Button>
                </div>
            </div>
        );
    }
}

export default withStyles(styles, { withTheme: true })(Loading);
