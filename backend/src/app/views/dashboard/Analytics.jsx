import React, { Component, Fragment } from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import { SimpleCard } from "matx";


// import DoughnutChart from "../charts/echarts/Doughnut";
//
// import ModifiedAreaChart from "./shared/ModifiedAreaChart";
// import StatCards from "./shared/StatCards";
// import TableCard from "./shared/TableCard";
// import RowCards from "./shared/RowCards";
// import StatCards2 from "./shared/StatCards2";
// import UpgradeCard from "./shared/UpgradeCard";
// import Campaigns from "./shared/Campaigns";
import { withStyles } from "@material-ui/styles";
import {makeStyles} from "@material-ui/core/styles";


class Dashboard1 extends Component {
  state = {};

  render() {;
      const classes = makeStyles;
      let { theme } = this.props;

    return (
      <Fragment>
        <div className="pb-24 pt-7 px-18 bg-primary">
          <div className="card-title capitalize text-white mb-1 text-white-secondary">
            New Midi Project
          </div>
        </div>
          <SimpleCard>
              <div className="card-title capitalize text-white mb-1 text-primary">
                Upload Midi File
            </div>
              <br/>
              <Fab
                  size="small"
                  color="secondary"
                  aria-label="Add"
                  className={classes.button}
              >
                  <Icon>add</Icon>
              </Fab>
          </SimpleCard>
      </Fragment>
    );
  }
}

export default withStyles({}, { withTheme: true })(Dashboard1);
