import React, {Component} from "react";
import {SimpleCard} from "matx";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import { Grid, Container } from '@material-ui/core';
import 'react-piano/dist/styles.css';
import PlaybackDemo from "../services/PlaybackDemo";
import { lostWoods } from '../services/songs';
import InteractiveDemo2 from "../services/InteractiveDemo2";



/**
 * This class defines the first view that a user will be presented with
 * The view will explain how to use our service and act as an about page for MIDISense
 *
 * Navigation:
 *      -> Upload
 */

class Upload extends Component {

    /**
     * The main constructor for the Welcome view
     *
     * @constructor
     * @param props
     */

    constructor(props){
        super(props);
        this.state = {}
    }

    /**
     * componentDidMount is invoked immediately after a component is mounted (inserted into the tree)
     */

    componentDidMount() {

    }

    /**
     * shouldComponentUpdate lets React know if a component’s output is not affected by the current change in state
     * or props. In our case, true.
     *
     * @param nextProps
     * @param nextState
     * @param nextBool
     * @returns {boolean}
     */

    shouldComponentUpdate(nextProps, nextState, nextBool) {
        return true;
    }

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
            <div className="m-sm-30" >
                <SimpleCard>
                    <div className="mt-5">
                        <InteractiveDemo2 audioContext={audioContext} soundfontHostname={soundfontHostname} />
                        <br/>
                        <PlaybackDemo
                            audioContext={audioContext}
                            soundfontHostname={soundfontHostname}
                            song={lostWoods}
                        />
                        <br/>
                    </div>
                <Grid container justify="space-evenly" spacing={3}>
                    <Grid item >
                        <div className={"max-w-400"}>
                            <img src={process.env.PUBLIC_URL + '/assets/images/logo-full.png'} alt={"MidiSense Logo"}/>
                        </div>
                    </Grid>
                <Container>
                    <h3>About Us</h3>
                    <br/>
                        <Grid item >
                            <div className={"max-w-400 min-w-200"}>
                                <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Laptop_character_2.svg'} alt={"Person on laptop"}/>
                            </div>
                        </Grid>
                        <Grid item >
                            <div className="text-14">
                                <p>
                                    This system enables the interpretation, display, playback and analysis of musical data
                                    stored in a midi format.
                                </p>
                                <p>
                                    Music forms an integral part of modern society. It is without a doubt, the most
                                    ubiquitous of art forms, permeating through the day-to-day lives of most individuals in
                                    some form or another and is one that most people would find challenging to live without.
                                </p>
                                <p>
                                    As with most artistic disciplines, the accessibility of music and a rise in music
                                    education among the youth have made its practises an appropriate target for rapid
                                    digitisation. As the composition, arrangement and transcription of works are common
                                    industry practises, there has been a drive in recent years to marry these processes
                                    with technologies that enhance both the sensory and extrasensory perception of a work
                                    during such tasks.
                                </p>
                            </div>
                            <br/>
                        </Grid>
                </Container>
                </Grid>

                <br/>
                    <Grid container justify="space-evenly" spacing={3} >
                        <Grid item >
                            <h3>How To Use MIDISense</h3>
                            <br/>
                            <div className="text-16">
                                <li>Navigate to the Upload page using the navigation bar on the left</li>
                                <li>Find a midi file you want to know more about.</li>
                                <li>Drag and drop your file onto the provided area or simply click on the area to
                                    navigate to the file using an explorer.</li>
                                <li>Hit the process button!</li>
                                <li>Let our system do it's magic.</li>
                                <li>Look through all the details of your file.</li>
                            </div>
                            <br/>
                        </Grid>
                        <Grid item >
                            <div className={"max-w-300 min-w-200"}>
                                <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Singing_character.svg'} alt={"Person singing"}/>
                            </div>
                        </Grid>
                    </Grid>
                </SimpleCard>
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Upload);
