import React, {Component} from "react";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import {Grid, Container, Button} from '@material-ui/core';
import 'react-piano/dist/styles.css';

import InteractiveDemo2 from "../services/InteractiveDemo2";
import Background from "./Background";




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
                                        onClick={() => this.props.history.push("/Upload")}
                                    >
                                        <div className="text-16">
                                            Upload your own file
                                        </div>
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button
                                        className="capitalize"
                                        variant="contained"
                                        color="secondary"
                                        onClick={() => this.props.history.push("/About")}
                                    >
                                        <div className="text-16">
                                            About us
                                        </div>
                                    </Button>
                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item>
                            <div className="w-full-screen ">
                                <InteractiveDemo2 audioContext={audioContext} soundfontHostname={soundfontHostname} />
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
