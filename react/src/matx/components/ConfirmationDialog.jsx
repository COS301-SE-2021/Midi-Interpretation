import React from "react";
import { Dialog, Button } from "@material-ui/core";

const ConfirmationDialog = ({
  open,
  onConfirmDialogClose,
  text = "Cookies allow us to keep track of the files you've uploaded and the interpretation our system has " +
  "generated for you. We do not track your browsing, identity or give information to advertisers. Please consent to " +
  "the cookies as stipulated to continue.",
  title = "We Use Cookies",
  onYesClick
}) => {
  return (
    <Dialog
      maxWidth="xs"
      fullWidth={true}
      open={open}
      onClose={onConfirmDialogClose}
    >
      <div className="p-5 pb-2">
        <h4 className="capitalize m-0 mb-2">{title}</h4>
        <p>{text}</p>
        <div className="flex justify-between pt-2">
          <Button onClick={onYesClick} variant="contained" color="primary">
            Yes
          </Button>
          <Button
            onClick={onConfirmDialogClose}
            variant="contained"
            color="secondary"
          >
            No
          </Button>
        </div>
      </div>
    </Dialog>
  );
};

export default ConfirmationDialog;
