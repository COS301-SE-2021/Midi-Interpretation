import React, { Component } from "react";
import { withStyles } from "@material-ui/styles";
import CircularProgress from "@material-ui/core/CircularProgress";
import 'react-piano/dist/styles.css';
import InteractiveDemo from "../services/InteractiveDemo";
import Typical from 'react-typical'

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
        const lyrics = [
            "Don't you know I heard it through the grapevine? Not much longer would you be mine...",

            "It's just an invitation across the nation. A chance for folks to meet...",

            "Bo Diddley bought a nanny goat. To make his pretty baby a Sunday coat...",

            "Well it took my baby, but it never will again. No, not again...",

            "And it burns, burns, burns. The ring of fire...",

            "Pleased to meet you. Hope you guess my name...",

            "And I forget just why I taste. Oh yeah, I guess it makes me smile...",

            "There's a lady who's sure all that glitters is gold. And she's buying a stairway to heaven...",

            "She says she loves you. And you know that can't be bad...",

            "Sometimes I wonder what I'm a gonna do. But there ain't no cure for the summertime blues...",

            "We can be Heroes. Just for one day...",

            "it's like a jungle sometimes, it makes me wonder. How I keep from going under...",

            "Though we're apart. You're part of me still. For you were my thrill. On Blueberry Hill...",

            "Oh baby, love and happiness...",

            "Good golly Miss Molly, sure like to ball...",

            "I can't get no satisfaction. I try and I try and I try and I try. I can't get no, I can't get no...",

            "How can you just leave me standing. Alone in a world so cold...",

            "Hey, Jude, don't make it bad. Take a sad song. And make it better...",

            "Everybody says, \"Let's, let's stay together\". I'll keep on lovin' you whether, whether...",

            "My girl, Talkin bout my girl...",

            "PURPLE HAZE ALL AROUND. DON'T KNOW IF I'M COMIN' UP OR DOWN..",

            "Plenty of room at the Hotel California. Any time of year. You can find it here...",

            "And don't you step on my blue suede shoes. You can do anything but lay off of my blue suede shoes...",

            "I keep the ends out for the tie that binds. Because you're mine, I walk the line...",

            "Girl, you really got me goin'. You got me so I don't know what I'm doin'...",

            "If you should ever leave me. Though life would still go on, believe me...",

            "Maybellene, why can't you be true? Oh maybellene, why can't you be true...",
        ]
        function shuffleArray(array) {
            for (let i = array.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1))
                const temp = array[i]
                array[i] = array[j]
                array[j] = temp
            }
            let result = []
            for (let i = array.length - 1; i > 0; i--) {
                result.push(lyrics[i])
                result.push(1000)
            }
            return result
        }
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
                    <Typical
                        steps={shuffleArray(lyrics)}
                        loop={Infinity}
                        wrapper="b"
                    />
                    <br/>
                    <div className="w-full-screen ">
                        <InteractiveDemo audioContext={audioContext} soundfontHostname={soundfontHostname} />
                    </div>
                </div>
            </div>
        );
    }
}

export default withStyles(styles, { withTheme: true })(Loading);
