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
 * TODO: flesh out commenting
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

const Layout1 = props => {
  const { routes } = useContext(AppContext);
  let { settings, classes, theme } = props;
  let { layout1Settings } = settings;
  let layoutClasses = {
    [classes.layout]: true,
    [`${settings.activeLayout} sidenav-${layout1Settings.leftSidebar.mode} theme-${theme.palette.type} flex`]: true
  };

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

Layout1.propTypes = {
  settings: PropTypes.object.isRequired
};

const mapStateToProps = state => ({
  setLayoutSettings: PropTypes.func.isRequired,
  settings: state.layout.settings
});

export default withStyles(styles, { withTheme: true })(
  connect(mapStateToProps, { setLayoutSettings })(Layout1)
);
