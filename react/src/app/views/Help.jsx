import React, {Component} from "react";
import {Breadcrumb} from "matx";
import { withStyles } from "@material-ui/styles";
import 'react-responsive-combo-box/dist/index.css';
import { Grid } from '@material-ui/core';

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
                            <h4>How To Use MIDISense</h4>
                            <br/>
                            <div className="text-20">
                                <li>Navigate to the Upload page using the navigation bar on the left.</li>
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
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Help);
