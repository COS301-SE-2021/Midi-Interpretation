import React, { useContext } from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import { withStyles, ThemeProvider } from "@material-ui/core/styles";
import Scrollbar from "react-perfect-scrollbar";
import { classList } from "utils";
import { renderRoutes } from "react-router-config";
import Layout1Sidenav from "./Layout1Sidenav";
import AppContext from "app/appContext";
import { MatxSuspense } from "matx";

/**
 * The styling for the layout
 *
 * @param theme
 * @returns {{layout: {backgroundColor}}}
 */

const styles = theme => {
  return {
    layout: {
      backgroundColor: theme.palette.background.default
    }
  };
};

/**
 * Layout 1 that can be swapped out for another layout
 * Defines the common parts of a the layout
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */

const Layout1 = props => {
  const { routes } = useContext(AppContext);
  let { settings, classes, theme } = props;
  let { layout1Settings } = settings;
  let layoutClasses = {
    [classes.layout]: true,
    [`${settings.activeLayout} sidenav-${layout1Settings.leftSidebar.mode} theme-${theme.palette.type} flex`]: true
  };

  /**
   * The UI representation being returned
   */

  return (
    <div className={classList(layoutClasses)}>
      {layout1Settings.leftSidebar.show && <Layout1Sidenav />}

      <div className="content-wrap position-relative">

        {settings.perfectScrollbar && (
          <Scrollbar className="scrollable-content">
            <div className="content">
              <MatxSuspense>{renderRoutes(routes)}</MatxSuspense>
            </div>
            <div className="my-auto" />
          </Scrollbar>
        )}

        {!settings.perfectScrollbar && (
          <div className="scrollable-content">
            <div className="content">
              <MatxSuspense>{renderRoutes(routes)}</MatxSuspense>
            </div>
            <div className="my-auto" />
          </div>
        )}

      </div>
    </div>
  );
};

/**
 * Layout 1 props
 */

Layout1.propTypes = {
  settings: PropTypes.object.isRequired
};

/**
 * mapping state to props
 * @param state
 */

const mapStateToProps = state => ({
  setLayoutSettings: PropTypes.func.isRequired,
  settings: state.layout.settings
});

/**
 * Export with styles
 */

export default withStyles(styles, { withTheme: true })(
  connect(mapStateToProps, { setLayoutSettings })(Layout1)
);
