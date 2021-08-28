import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { useTheme } from "@material-ui/core/styles";
import Cookies from "universal-cookie";

/**
 * The confirmation dialog to allow for cookie gathering
 *
 * @returns {JSX.Element}
 * @constructor
 */

export default function ResponsiveDialog() {
  const [open, setOpen] = React.useState(true)
  const theme = useTheme()
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"))
  const cookies = new Cookies()

  /**
   * close the dialog
   */
  function handleClose() {
    setOpen(false)
  }

  /**
   * User consent is given
   */
  function confirm(){
    let end = new Date()
    end.setDate(end.getDate()+365);

    cookies.set('allowCookies', 'true', { path: '/',
      expires:end
    })
    window.location.reload(false)
    handleClose()
  }

  return (
    <div>
      <Dialog
        fullScreen={fullScreen}
        open={open}
        onClose={handleClose}
        aria-labelledby="responsive-dialog-title"
      >
        <DialogTitle id="responsive-dialog-title">
          {"We Use Cookies!"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            Cookies allow us to keep track of the files you've uploaded and the interpretation our system has generated
            for you. We do not track your browsing, identity or give information to advertisers. Please consent to the
            cookies as stipulated to continue.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} style={{color:"#ff9e43"}}>
            Disagree
          </Button>
          <Button onClick={confirm} color="primary" autoFocus>
            Agree
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
