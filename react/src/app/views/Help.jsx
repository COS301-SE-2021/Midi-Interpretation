import React, {Component} from "react";
import {Breadcrumb} from "matx";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import {Avatar, Grid, Icon} from '@material-ui/core';
import {GitHub} from "@material-ui/icons";

/**
 * The view will explain how to use MIDISense
 *
 * Navigation:
 *      -> Upload
 */

class Help extends Component {

    /**
     * The main constructor for the Help view
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
                            { name: "Help" }
                        ]}
                    />
                </div>
                <br/>
                    <Grid
                        style={{marginLeft:"0px"}}
                        container
                        alignContent="space-between"
                        spacing={5}
                        className="bg-white">
                        <Grid item xs={12} sm={12} m={12} lg={8}>

                            <h3>Help</h3>
                            <br/>
                            <div>Our pages contain instructions to help you with the process of using MIDISense.
                                For more information about our team we suggest looking over our About page.
                                Our public repository and documentation is addresses many common issues but
                                don't be afraid to contact us or create an issue.</div>
                            <br/>

                            <Grid container direction="column"  spacing={5}>
                                <Grid item>
                                    <Grid container direction="row" spacing={5}>
                                        <Grid item>
                                            <Avatar variant="rounded" className="bg-primary">
                                                <GitHub color="#FFF"/>
                                            </Avatar>
                                        </Grid>
                                        <Grid item>
                                            <h5><a href="https://github.com/COS301-SE-2021/Midi-Interpretation">Repository.</a></h5>
                                            <aside className="text-muted font-medium">For technical information visit our public github repository.</aside>
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item>
                                    <Grid container direction="row" spacing={5}>
                                        <Grid item>
                                            <Avatar variant="rounded" className="bg-primary">
                                                <Icon color="#FFF">bug_report</Icon>
                                            </Avatar>
                                        </Grid>
                                        <Grid item>
                                            <h5><a href="https://github.com/COS301-SE-2021/Midi-Interpretation/issues">Issues.</a></h5>
                                            <aside className="text-muted font-medium">Have any major issues? Check out our issues page to report them. </aside>
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item>
                                    <Grid container direction="row" spacing={5}>
                                        <Grid item>
                                            <Avatar variant="rounded" className="bg-primary">
                                                <Icon color="#FFF">menu_book</Icon>
                                            </Avatar>
                                        </Grid>
                                        <Grid item>
                                            <h5><a href="https://www.overleaf.com/read/ddyfqzkcspzd">User Manual.</a></h5>
                                            <aside className="text-muted font-medium">Our user manual is a great place to learn how to use MIDISense.</aside>
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item>
                                    <Grid container direction="row" spacing={5}>
                                        <Grid item>
                                            <Avatar variant="rounded" className="bg-primary">
                                                <Icon color="#FFF">person</Icon>
                                            </Avatar>
                                        </Grid>
                                        <Grid item>
                                            <h5>Contact Us.</h5>
                                            <aside className="text-muted font-medium">Contact us directly here: <span className="text-primary">noexceptionteam@gmail.com</span></aside>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>

                        </Grid>
                        <Grid item xs={12} sm={12} m={12} lg={4}>
                            <div className={"max-w-300 min-w-200"}>
                                <img src={process.env.PUBLIC_URL + '/assets/images/illustrations/Laptop_character_3.svg'} alt={"Person singing"}/>
                            </div>
                        </Grid>
                    </Grid>
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Help);
