import React, { Fragment } from "react";
import Scrollbar from "react-perfect-scrollbar";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { navigations } from "../../navigations";
import { MatxVerticalNav } from "matx";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";

/**
 * The default sidebar of the application
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */

const Sidenav = props => {
  const updateSidebarMode = sidebarSettings => {
    let { settings, setLayoutSettings } = props;
    let activeLayoutSettingsName = settings.activeLayout + "Settings";
    let activeLayoutSettings = settings[activeLayoutSettingsName];

    setLayoutSettings({
      ...settings,
      [activeLayoutSettingsName]: {
        ...activeLayoutSettings,
        leftSidebar: {
          ...activeLayoutSettings.leftSidebar,
          ...sidebarSettings
        }
      }
    });
  };

  /**
   * Render the sidebar as an overlay
   * @returns {JSX.Element}
   */

  const renderOverlay = () => (
    <div
      onClick={() => updateSidebarMode({ mode: "close" })}
      className="sidenav__overlay"
    />
  );

  /**
   * The UI representation being returned
   */

  return (
    <Fragment>
      <Scrollbar
        options={{ suppressScrollX: true }}
        className="scrollable position-relative"
      >
        {props.children}
        {<MatxVerticalNav navigation={navigations} />}
      </Scrollbar>
      {renderOverlay()}
    </Fragment>
  );
};

/**
 * Sidenav props
 */

Sidenav.propTypes = {
  setLayoutSettings: PropTypes.func.isRequired,
  settings: PropTypes.object.isRequired
};

/**
 * Mapping state to props
 * @param state
 */

const mapStateToProps = state => ({
  setLayoutSettings: PropTypes.func.isRequired,
  settings: state.layout.settings
});

/**
 * Export with router
 */

export default withRouter(
  connect(mapStateToProps, {
    setLayoutSettings
  })(Sidenav)
);
