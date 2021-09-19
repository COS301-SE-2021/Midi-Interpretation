/**
 * External from MIDISense
 */

import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import CircularProgress from "@material-ui/core/CircularProgress";

const useStyles = makeStyles(theme => ({
  loading: {
    position: "fixed",
    left: 0,
    right: 0,
    top: "calc(50% - 20px)",
    margin: "auto",
    height: "10%",
    width: "10%",
  }
}));

const Loading = props => {
  const classes = useStyles();

  return (
    <div className={classes.loading}>
      <CircularProgress color={"primary"} />
    </div>
  );
};

export default Loading;
