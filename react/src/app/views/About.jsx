import React, {Component} from "react";
import {Breadcrumb} from "matx";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import {Avatar, Grid} from '@material-ui/core';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import AppBar from '@material-ui/core/AppBar';

/**
 * The view will act as an about page for MIDISense and give information on
 *
 * - Our development team
 * - How we gathered the data for your AI
 * - Credits for the external elements of our system
 *
 */

/**
 * The tab navigation panel
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */

function TabPanel(props) {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`scrollable-auto-tabpanel-${index}`}
            aria-labelledby={`scrollable-auto-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box p={3}>
                    <Typography>{children}</Typography>
                </Box>
            )}
        </div>
    );
}

/**
 * TabPanel Prop type definition
 */

TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.any.isRequired,
    value: PropTypes.any.isRequired,
};

/**
 * a11yProps
 *
 * @param index
 * @returns {{"aria-controls": string, id: string}}
 */

function a11yProps(index) {
    return {
        id: `scrollable-auto-tab-${index}`,
        'aria-controls': `scrollable-auto-tabpanel-${index}`,
    };
}

/**
 * Styling information for the view
 */

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
        width: '100%',
        backgroundColor: theme.palette.background.paper,
    },
}));

/**
 * AboutTabs adds the content to the tabs presented to the user by TabPanel
 *
 * @returns {JSX.Element}
 * @constructor
 */

function AboutTabs() {
    const classes = useStyles();
    const [value, setValue] = React.useState(0);

    // Change view to the new panel contents
    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <div className={classes.root}>
            <AppBar position="static" color="default">
                <Tabs
                    value={value}
                    onChange={handleChange}
                    indicatorColor="primary"
                    textColor="primary"
                    variant="scrollable"
                    scrollButtons="auto"
                    aria-label="scrollable auto tabs example"
                >
                <Tab label="About Our Project" {...a11yProps(0)} />
                <Tab label="How We Gathered Data" {...a11yProps(1)} />
                <Tab label="Credits" {...a11yProps(2)} />
            </Tabs>
            </AppBar>
            <TabPanel value={value} index={0}>
                        <div className="text-20">
                            <br/>

                            <div className="font-bold">Our Team</div>

                            <br/>
                            <Grid container justifyContent="space-evenly" direction="row">
                                <Grid item>
                                    <Grid container direction="column"  spacing={5} justifyContent="space-between">
                                        <Grid item>
                                            <Grid container direction="row" spacing={5}>
                                                <Grid item>
                                                    <Avatar variant="rounded" alt="Remy Sharp" src={process.env.PUBLIC_URL + '/assets/images/hendro.png'}/>
                                                </Grid>
                                                <Grid item>
                                                    <h4>Hendro Smit | <a href="https://github.com/hendrosmit">GitHub</a> | <a href="https://www.linkedin.com/in/hendro-smit-328ba720b/">LinkedIn</a></h4>
                                                    <aside className="text-muted font-medium">Front-end lead and UX engineer</aside>
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                        <Grid item>
                                            <Grid container direction="row" spacing={5}>
                                                <Grid item>
                                                    <Avatar variant="rounded" alt="Remy Sharp" src={process.env.PUBLIC_URL + '/assets/images/claudio.jpg'}/>
                                                </Grid>
                                                <Grid item>
                                                    <h4>Claudio Teixeira | <a href="https://github.com/Claudio-Uni">GitHub</a> | <a href="https://www.linkedin.com/in/claudio-teixeira-b9bb9820b/">LinkedIn</a></h4>
                                                    <aside className="text-muted font-medium">Business analyst and admin lead</aside>
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                        <Grid item>
                                            <Grid container direction="row" spacing={5}>
                                                <Grid item>
                                                    <Avatar variant="rounded" alt="Remy Sharp" src={process.env.PUBLIC_URL + '/assets/images/rea.jpg'}/>
                                                </Grid>
                                                <Grid item>
                                                    <h4>Rearabetswe Maeko | <a href="https://github.com/u18094024">GitHub</a> | <a href="https://www.linkedin.com/in/rea-maeko-0b5a4a20b/">LinkedIn</a></h4>
                                                    <aside className="text-muted font-medium">Backend developer and tester</aside>
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                        <Grid item>
                                            <Grid container direction="row" spacing={5}>
                                                <Grid item>
                                                    <Avatar variant="rounded" alt="Remy Sharp" src={process.env.PUBLIC_URL + '/assets/images/mbuso.jpg'}/>
                                                </Grid>
                                                <Grid item>
                                                    <h4>Mbuso Shakoane | <a href="https://github.com/u18094024">GitHub</a> | <a href="https://www.linkedin.com/in/mbuso-shakoane-049a4920b/">LinkedIn</a></h4>
                                                    <aside className="text-muted font-medium">Backend developer and tester</aside>
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                        <Grid item>
                                            <Grid container direction="row" spacing={5}>
                                                <Grid item>
                                                    <Avatar variant="rounded" alt="Remy Sharp" src={process.env.PUBLIC_URL + '/assets/images/adrian.jpg'}/>
                                                </Grid>
                                                <Grid item>
                                                    <h4>Adrian Rae | <a href="https://github.com/Adrian-Rae-19004029">GitHub</a> | <a href="https://www.linkedin.com/in/adrian-rae-5796b31bb/">LinkedIn</a></h4>
                                                    <aside className="text-muted font-medium">System architect and backend lead</aside>
                                                </Grid>
                                            </Grid>
                                        </Grid>
                                        <br/>
                                    </Grid>
                                </Grid>
                                <Grid item>
                                    <img style={{width:"500px"}} src={process.env.PUBLIC_URL + '/assets/images/logo-full.png'} alt={"MidiSense Logo"}/>
                                </Grid>
                            </Grid>

                            <br/>

                            <div className="font-bold">Our Project</div>

                            <br/>

                            <div>
                                NoXception is a team of five third-year EBIT undergraduates from the University of
                                Pretoria with a determination to embody best practices in real-world software
                                engineering. We have the drive to engage in development projects which challenge us
                                on a professional basis and enrich us on a personal level.
                            </div>
                            <br/>
                            <div>
                                We were tasked to create a System that would be able to Interpret Midi music files
                                and display gathered information. This is the system you are currently using
                                This system enables the interpretation, display and analysis of musical data stored
                                in a midi format.
                            </div>
                            <br/>
                            <div>
                                Music forms an integral part of modern society. It is without a doubt, the most
                                ubiquitous of art forms, permeating through the day-to-day lives of most
                                individuals in some form or another and is one that most people would find
                                challenging to live without.
                            </div>
                            <br/>
                            <div>
                                As with most artistic disciplines, the accessibility
                                of music and a rise in music education among the youth have made its practises an
                                appropriate target for rapid digitisation. As the composition, arrangement and
                                transcription of works are common industry practises, there has been a drive in
                                recent years to marry these processes with technologies that enhance both the
                                sensory and extrasensory perception of a work during such tasks.
                            </div>
                        </div>
            </TabPanel>
            <TabPanel value={value} index={1}>
                <br/>
                <Grid container
                      alignContent="center"
                      justifyContent="space-evenly"
                >

                    <Grid item>
                        <div style={{width:"150px"}}>
                            <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Forward_character.svg'} alt={"Person on laptop"}/>
                        </div>
                    </Grid>

                    <Grid item>
                        <div className="font-bold text-20">How We Gathered Data</div>
                        <div className="text-20">
                            <span>In order to train our system AI we needed to gather a thousands of midi files.</span>
                            <li>First, we went to <a href="https://freemidi.org/">Free Midi</a> and gathered a list of all
                                it's tracks, their authors and download links</li>
                            <li>Next, we categorized the tracks based on their name and author using the
                                <a href="https://developer.spotify.com/documentation/web-api/"> Spotify API </a></li>
                            <li>This data now needed to be cleaned and pruned to allow for more general and specific
                            genre classification</li>
                            <li>Finally, the data is ready to train our artificial neural network and help classify
                                your midi files</li>
                        </div>
                        <br/>
                    </Grid>
                </Grid>
            </TabPanel>
            <TabPanel value={value} index={2}>
                <br/>
                <Grid container
                      alignContent="center"
                      justifyContent="space-evenly"
                      spacing={5}>
                    <Grid item>
                        <div className="font-bold text-20">Credits</div>
                        <br/>
                        <div className="text-20">
                            <li><a href="https://github.com/uilibrary/matx-react">
                                MatX React Material Dashboard</a> By <a href="https://ui-lib.com/">UI Lib</a>
                                &emsp;[<a href="https://ui-lib.com/downloads/matx-react-dashboard/">License</a>]</li>
                            <li><a href="https://www.drawkit.io/product/people-working-collaborating-illustrations">
                                People Working Illustrations</a> By <a href="https://www.drawkit.io/">DrawKit</a>
                                &emsp;[<a href="https://www.drawkit.io/license">License</a>]</li>
                            <li><a href="https://recharts.org/en-US">
                                Recharts</a> By <a href="https://github.com/recharts/recharts">Recharts Group</a>
                                &emsp;[<a href="https://github.com/recharts/recharts/blob/master/LICENSE">License</a>]</li>
                            <li><a href="https://github.com/matteobruni/tsparticles"> ts Particles</a> By
                                <a href="https://github.com/matteobruni"> Matteo Bruni</a>
                                &emsp;[<a href="https://github.com/matteobruni/tsparticles/blob/main/LICENSE">License</a>]</li>
                            <li><a href="https://github.com/kevinsqi/react-piano">React-Piano </a>
                                By <a href="https://github.com/kevinsqi">Kevin Qi</a>
                                &emsp;[<a href="https://github.com/kevinsqi/react-piano/blob/master/LICENSE">License</a>]</li>
                            <li><a href="http://www.jfugue.org/">
                                JFugue</a> By <a href="https://github.com/dmkoelle">David Koelle</a>
                                &emsp;[<a href="https://github.com/dmkoelle/jfugue">License</a>]</li>
                        </div>
                        <br/>
                    </Grid>
                    <Grid item>
                        <div style={{width:"250px"}}>
                            <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Thumbs_Up_character.svg'} alt={"Person singing"}/>
                        </div>
                    </Grid>
                </Grid>
            </TabPanel>
        </div>
    );
}

/**
 * The core About component
 */

class About extends Component {

    /**
     * The main constructor for the About view
     *
     * @constructor
     * @param props
     */

    constructor(props){
        super(props);
        this.state = {}
    }

    /**
     * This method returns the elements that we want displayed
     *
     * REQUIRED
     *
     * @returns {JSX.Element}
     */

    render() {
        return (
            <div className="m-sm-30" >
                <div className="mb-sm-30">
                    <Breadcrumb
                        routeSegments={[
                            { name: "About Us" }
                        ]}
                    />
                </div>
                    <div>
                        <AboutTabs/>
                    </div>
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(About);
