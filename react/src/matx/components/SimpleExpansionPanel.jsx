import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpansionPanelDetails from "@material-ui/core/ExpansionPanelDetails";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import {Icon, Tooltip} from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";

/**
 * Styling for the component
 */

const useStyles = makeStyles(theme => ({
  root: {
    width: "100%"
  },
  heading: {
    fontSize: theme.typography.pxToRem(15),
    fontWeight: theme.typography.fontWeightRegular
  }
}));

/**
 * An expansion panel that allows the hiding and display of UI elements
 *
 * @returns {JSX.Element}
 * @constructor
 */

export default function SimpleExpansionPanel() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <ExpansionPanel>
        <ExpansionPanelSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel1a-content"
          id="panel1a-header"
        >
          Notes
        </ExpansionPanelSummary>
        <ExpansionPanelDetails>
          <Tooltip title={
            <React.Fragment>
              <Typography color="inherit">Note</Typography>
              <li>Pitch: 12</li>
              <li>Velocity: 212</li>
              <li>Octave Value: 3</li>
            </React.Fragment>
          }>
            <IconButton
                color="secondary"
                className={classes.button}
                aria-label="Add an alarm"
            >
              <Icon>music_note</Icon>
            </IconButton>
          </Tooltip>
          <Tooltip title={
            <React.Fragment>
              <Typography color="inherit">Note</Typography>
              <li>Pitch: 12</li>
              <li>Velocity: 212</li>
              <li>Octave Value: 3</li>
            </React.Fragment>
          }>
            <IconButton
                color="secondary"
                className={classes.button}
                aria-label="Add an alarm"
            >
              <Icon>music_note</Icon>
            </IconButton>
          </Tooltip>
          <Tooltip title={
            <React.Fragment>
              <Typography color="inherit">Note</Typography>
              <li>Pitch: 12</li>
              <li>Velocity: 212</li>
              <li>Octave Value: 3</li>
            </React.Fragment>
          }>
            <IconButton
                color="secondary"
                className={classes.button}
                aria-label="Add an alarm"
            >
              <Icon>music_note</Icon>
            </IconButton>
          </Tooltip>
        </ExpansionPanelDetails>
      </ExpansionPanel>
    </div>
  );
}
