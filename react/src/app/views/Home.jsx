import React, {Component} from "react";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import {Grid, Button} from '@material-ui/core';
import InteractiveDemo from "../services/InteractiveDemo";
import Background from "./Background";
import MdArrowDownward from "@material-ui/icons/ArrowDownward";
import "../services/styles.css"



/**
 * This class defines the first view that a user will be presented with
 * The view will explain how to use our service and act as an about page for MIDISense
 *
 * Navigation:
 *      -> Upload
 */

class Home extends Component {

    /**
     * This method returns the elements that we want displayed
     *
     * REQUIRED
     *
     * @returns {JSX.Element}
     */

    render() {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const soundfontHostname = 'https://d1pzp51pvbm36p.cloudfront.net';

        return (
            <div>
                <div style={{
                    position:"relative",
                    zIndex: 1
                }}
                className="text-white mt-6"
                >
                    <Grid container justify="space-between" alignItems="center" spacing={3} direction="column">
                        <Grid item>
                            <div className={"max-w-400"}>
                                <img src={process.env.PUBLIC_URL + '/assets/images/logo-full-dark.png'} alt={"MidiSense Logo"}/>
                            </div>
                        </Grid>
                        <Grid item>
                            <Grid container justify="space-between" spacing="2">
                                <Grid item>
                                    <Button
                                        className="capitalize"
                                        variant="contained"
                                        color="secondary"
                                        onClick={() => this.props.history.push("/About")}
                                    >
                                        <div className="text-16 font-bold">
                                            About us
                                        </div>
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button
                                        className="capitalize"
                                        variant="contained"
                                        color="secondary"
                                        onClick={() => this.props.history.push("/Upload")}
                                    >
                                        <div className="text-16 font-bold">
                                            Use MIDISense
                                        </div>
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button
                                        className="capitalize"
                                        variant="contained"
                                        color="secondary"
                                        onClick={() => this.props.history.push("/Help")}
                                    >
                                        <div className="text-16 font-bold">
                                            Help
                                        </div>
                                    </Button>
                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item>
                            <div className="w-full-screen ">
                                <div className="text-center">
                                    <h4 className="text-white">Welcome To MIDISense</h4>
                                    <p className="text-16">Upload and process midi files to learn more about them,
                                        learn more about our project or just enjoy the piano bellow</p>
                                    <div style={{ color: '#777' }}>
                                        <MdArrowDownward size={32} />
                                    </div>
                                </div>
                                <InteractiveDemo audioContext={audioContext} soundfontHostname={soundfontHostname} />
                            </div>
                        </Grid>
                    </Grid>
                </div>
                <div style={{
                    position:"relative",
                    zIndex: 0
                }}>
                    <Background/>
                </div>
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Home);
