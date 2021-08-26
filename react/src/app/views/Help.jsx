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

class Help extends Component {

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
                            { name: "Help" }
                        ]}
                    />
                </div>
                <SimpleCard>

                </SimpleCard>
            </div>
        );
    };
}


/**
 * The final export of our view
 */

export default withStyles({}, { withTheme: true })(Help);
