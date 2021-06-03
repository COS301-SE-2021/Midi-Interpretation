import React, { Component, Fragment } from "react";
import {Grid, Card, Button} from "@material-ui/core";
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
      const classes = withStyles(1);
      let { theme } = this.props;

    return (
      <Fragment>
        <div className="pb-24 pt-7 px-8 bg-primary">
          <div className="card-title capitalize text-white mb-4 text-white-secondary">
            MIDI File Upload
          </div>
        </div>
          <SimpleCard>
            <div className="analytics m-sm-30 mt--18">
                <label htmlFor="contained-button-file">
                    <Button
                        variant="contained"
                        component="span"
                        className={classes.button}
                    >
                        Upload
                    </Button>
                </label>
            </div>
          </SimpleCard>
      </Fragment>
    );
  }
}

export default withStyles({}, { withTheme: true })(Dashboard1);
