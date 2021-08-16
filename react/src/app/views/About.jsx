import React, {Component} from "react";
import {Breadcrumb, SimpleCard} from "matx";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import { Grid, Container } from '@material-ui/core';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import AppBar from '@material-ui/core/AppBar';

/**
 * This class defines the first view that a user will be presented with
 * The view will explain how to use our service and act as an about page for MIDISense
 *
 * Navigation:
 *      -> Upload
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

TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.any.isRequired,
    value: PropTypes.any.isRequired,
};

function a11yProps(index) {
    return {
        id: `scrollable-auto-tab-${index}`,
        'aria-controls': `scrollable-auto-tabpanel-${index}`,
    };
}

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
        width: '100%',
        backgroundColor: theme.palette.background.paper,
    },
}));

function AboutTabs() {
    const classes = useStyles();
    const [value, setValue] = React.useState(0);

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
                <Tab label="How To Use MIDISense" {...a11yProps(1)} />
                <Tab label="How We Gathered Data" {...a11yProps(2)} />
                <Tab label="Credits" {...a11yProps(3)} />
            </Tabs>
            </AppBar>
            <TabPanel value={value} index={0}>
                <Grid container
                      alignContent="space-between"
                      justify="space-between"
                      spacing={5}>
                        <Grid  item xs={12} sm={12} m={12} lg={8} >
                            <div className="text-20">
                                <h4>About Us</h4>

                                <p>
                                    NoXception is a team of five third-year EBIT undergraduates from the University of
                                    Pretoria with a determination to embody best practices in real-world software
                                    engineering. We have the drive to engage in development projects which challenge us
                                    on a professional basis and enrich us on a personal level.
                                </p>
                                <p>
                                    We were tasked to create a System that would be able to Interpret Midi music files
                                    and display gathered information. This is the system you are currently using
                                    This system enables the interpretation, display and analysis of musical data stored
                                    in a midi format.
                                </p>
                                <p>
                                    Music forms an integral part of modern society. It is without a doubt, the most
                                    ubiquitous of art forms, permeating through the day-to-day lives of most
                                    individuals in some form or another and is one that most people would find
                                    challenging to live without.
                                </p>
                                <p>
                                    As with most artistic disciplines, the accessibility
                                    of music and a rise in music education among the youth have made its practises an
                                    appropriate target for rapid digitisation. As the composition, arrangement and
                                    transcription of works are common industry practises, there has been a drive in
                                    recent years to marry these processes with technologies that enhance both the
                                    sensory and extrasensory perception of a work during such tasks.
                                </p>
                            </div>
                            <br/>
                        </Grid>
                    <Grid  item xs={12} sm={12} m={12} lg={4} >
                    <div className={"max-w-300 min-w-100"}>
                        <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Forward_character.svg'} alt={"Person on laptop"}/>
                    </div>
                </Grid>
                </Grid>
            </TabPanel>
            <TabPanel value={value} index={1}>
                <br/>
                <Grid container
                      alignContent="space-between"
                      justify="space-between"
                      spacing={5}>
                    <Grid item xs={12} sm={12} m={12} lg={8}>
                        <h4>How To Use MIDISense</h4>
                        <br/>
                        <div className="text-20">
                            <li>Navigate to the Upload page using the navigation bar on the lef.t</li>
                            <li>Find a midi file you want to know more about.</li>
                            <li>Drag and drop your file onto the provided area or simply click to browse.</li>
                            <li>Wait for it to upload and then hit submit.</li>
                            <li>Hit the process button!</li>
                            <li>Let our system do it's magic.</li>
                            <li>Look through all the details of your file.</li>
                        </div>
                        <br/>
                    </Grid>
                    <Grid item xs={12} sm={12} m={12} lg={4}>
                        <div className={"max-w-300 min-w-200"}>
                            <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Laptop_character_3.svg'} alt={"Person singing"}/>
                        </div>
                    </Grid>
                </Grid>
            </TabPanel>
            <TabPanel value={value} index={2}>
                <br/>
                <Grid container
                      alignContent="space-between"
                      justify="space-between"
                      spacing={5}>
                    <Grid item xs={12} sm={12} m={12} lg={8}>
                        <h4>How We Gathered Data</h4>
                        <br/>
                        <div className="text-20">
                            <p>In order to train our system AI we needed to gather a thousands of midi files.</p>
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
                    <Grid item xs={12} sm={12} m={12} lg={4}>
                        <div className={"max-w-400 min-w-100"}>
                            <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Laptop_character_1.svg'} alt={"Person singing"}/>
                        </div>
                    </Grid>
                </Grid>
            </TabPanel>
            <TabPanel value={value} index={3}>
                <br/>
                <Grid container
                      alignContent="space-between"
                      justify="space-between"
                      spacing={5}>
                    <Grid item xs={12} sm={12} m={12} lg={8}>
                        <h4>Credits</h4>
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
                    <Grid item xs={12} sm={12} m={12} lg={4}>
                        <div className={"max-w-400 min-w-100"}>
                            <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Thumbs_Up_character.svg'} alt={"Person singing"}/>
                        </div>
                    </Grid>
                </Grid>
            </TabPanel>
        </div>
    );
}

class About extends Component {

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
        return (
            <div className="m-sm-30" >
                <div className="mb-sm-30">
                    <Breadcrumb
                        routeSegments={[
                            { name: "About Us" }
                        ]}
                    />
                </div>
                <SimpleCard>
                    <div>
                        <AboutTabs/>
                    </div>
                </SimpleCard>
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(About);
