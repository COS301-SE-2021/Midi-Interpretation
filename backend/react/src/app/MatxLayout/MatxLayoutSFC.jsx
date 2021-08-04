import React, { useContext, useEffect } from "react";
import { MatxLayouts } from "./index";
import PropTypes from "prop-types";
import { withRouter } from "react-router-dom";
import { matchRoutes } from "react-router-config";
import { connect } from "react-redux";
import AppContext from "app/appContext";
import {
  setLayoutSettings,
  setDefaultSettings
} from "app/redux/actions/LayoutActions";
import { isEqual, merge } from "lodash";
import { isMdScreen } from "utils";
import { MatxSuspense } from "matx";

let tempSettings;

/**
 * The functional aspect of a layout especially with regard to handling cross platform usage
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */

const MatxLayoutSFC = props => {
  let appContext = useContext(AppContext);
  const {
    settings,
    defaultSettings,
    setLayoutSettings,
    setDefaultSettings
  } = props;

  tempSettings = settings;

  /**
   * Window resizing listeners
   */

  useEffect(() => {
    listenWindowResize();

    if (window) {
      // LISTEN WINDOW RESIZE
      window.addEventListener("resize", listenWindowResize);
    }
    return () => {
      if (window) {
        window.removeEventListener("resize", listenWindowResize);
      }
    };
  }, []);

  /**
   * Use routing updater
   */

  useEffect(() => {
    updateSettingsFromRouter();
  }, [props.location]);

  /**
   * Window resizing
   */

  const listenWindowResize = () => {
    let settings = tempSettings;
    if (settings.layout1Settings.leftSidebar.show) {
      let mode = isMdScreen() ? "close" : "full";
      setLayoutSettings(
        merge({}, settings, { layout1Settings: { leftSidebar: { mode } } })
      );
    }
  };

  /**
   * Routing settings updater
   */

  const updateSettingsFromRouter = () => {
    const { routes } = appContext;
    const matched = matchRoutes(routes, props.location.pathname)[0];

    if (matched && matched.route.settings) {
      // ROUTE HAS SETTINGS
      const updatedSettings = merge({}, settings, matched.route.settings);
      if (!isEqual(settings, updatedSettings)) {
        setLayoutSettings(updatedSettings);
        // console.log('Route has settings');
      }
    } else if (!isEqual(settings, defaultSettings)) {
      setLayoutSettings(defaultSettings);
      // console.log('reset settings', defaultSettings);
    }
  };

  const Layout = MatxLayouts[settings.activeLayout];

  return (
    <MatxSuspense>
      <Layout {...props} />
    </MatxSuspense>
  );
};

/**
 * State to props mapping
 *
 * @param state
 */

const mapStateToProps = state => ({
  setLayoutSettings: PropTypes.func.isRequired,
  setDefaultSettings: PropTypes.func.isRequired,
  settings: state.layout.settings,
  defaultSettings: state.layout.defaultSettings
});

/**
 * Router
 */

export default withRouter(
  connect(mapStateToProps, { setLayoutSettings, setDefaultSettings })(
    MatxLayoutSFC
  )
);
