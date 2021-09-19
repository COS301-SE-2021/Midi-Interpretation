import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import MenuItem from "@material-ui/core/MenuItem";
import Menu from "@material-ui/core/Menu";

/**
 * Styling for the component
 */

const useStyles = makeStyles(theme => ({
    root: {
        width: "100%",
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper
    }
}));

/**
 * Menu for selecting an item from a dropdown menu.
 * Adds styling and functionality to @material-ui Menu
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */

export default function SelectedMenu(props) {
    const classes = useStyles();
    const [anchorEl, setAnchorEl] = React.useState(null);
    const [selectedIndex, setSelectedIndex] = React.useState(0);
    const options = props.inputOptions

    /**
     * Handle the list being clicked
     * @param event - The click event
     */

    function handleClickListItem(event) {
        setAnchorEl(event.currentTarget);
    }

    /**
     * Select an item on the menu, change the index of the current item and change the displayed item
     * @param event - The click event
     * @param index - the index of the selected menu item
     * @param option - name of the instrument
     */
    function handleMenuItemClick(event, index, option) {
        setSelectedIndex(index);
        props.setTrack(option)
        setAnchorEl(null);
    }

    /**
     * Close the menu
     */

    function handleClose() {
        setAnchorEl(null);
    }

    /**
     * The UI element that is returned to the view calling Selected Menu
     */

    return (
        <div className={classes.root}>
            <List component="nav" aria-label="Device settings">
                <ListItem
                    button
                    aria-haspopup="true"
                    aria-controls="instrument-menu"
                    aria-label="Select an instrument"
                    onClick={handleClickListItem}
                >
                    <ListItemText
                        primary="Instrument Selected"
                        secondary={options[selectedIndex]}
                    />
                </ListItem>
            </List>
            <Menu
                id="lock-menu"
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={handleClose}
            >
                {options.map((option, index) => (
                    <MenuItem
                        key={option}
                        selected={index === selectedIndex}
                        onClick={event => handleMenuItemClick(event, index, option)}
                    >
                        {option}
                    </MenuItem>
                ))}
            </Menu>
        </div>
    );
}
