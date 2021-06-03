import React, { Component, Fragment } from "react";
import {Grid, Card, Button, Fab, Icon} from "@material-ui/core";
import {Breadcrumb, SimpleCard} from "matx";


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
      const classes = makeStyles(theme => ({
          button: {
              margin: theme.spacing(1)
          },
          input: {
              display: "none"
          }
      }));
      let { theme } = this.props;
      return (
          <div className="m-sm-30">
              <div className="mb-sm-30">
                  <Breadcrumb
                      routeSegments={[
                          { name: "File Upload" }
                      ]}
                  />
              </div>
              <SimpleCard title="Prototype User Interface">

              </SimpleCard>
              <div className="py-3" />
              <SimpleCard title="Upload Midi File">
                  <label htmlFor="outlined-button-file">
                      <input
                          //accept=".md/*"
                          className={classes.input}
                          id="outlined-button-file"
                          type="file"
                      />
                      <Button

                          variant="outlined"
                          component="span"
                          className={classes.button}
                      >
                          Upload
                      </Button>
                  </label>
              </SimpleCard>
          </div>
      );
  };
  //   return (
  //     <Fragment>
  //       <div className="pb-24 pt-7 px-5 bg-primary">
  //         <div className="card-title capitalize text-white mb-1 text-white-secondary">
  //             New Midi Project
  //         </div>
  //       </div>
  //         <SimpleCard>
  //             <div className="card-title capitalize text-white mb-1 text-primary">
  //               Upload Midi File
  //           </div>
  //             <br/>
  //
  //         </SimpleCard>
  //     </Fragment>
  //   );
  // }
}

export default withStyles({}, { withTheme: true })(Dashboard1);
