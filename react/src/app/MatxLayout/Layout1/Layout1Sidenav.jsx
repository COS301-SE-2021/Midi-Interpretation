import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { Switch, Icon, MenuItem, Tooltip, IconButton } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import { connect } from "react-redux";
import { setLayoutSettings, setDefaultSettings} from "app/redux/actions/LayoutActions";
import { withRouter } from "react-router-dom";
import Sidenav from "../SharedCompoents/Sidenav";
import Brand from "../SharedCompoents/Brand";
import SidenavTheme from "../MatxTheme/SidenavTheme/SidenavTheme";
import { isMdScreen } from "utils";
import { merge } from "lodash";

/**
 * Styling for the layout 1 sidebar
 *
 * @param theme
 * @returns {{}}
 */

const styles = theme => ({});

/**
 *  Icon button white styling
 */

const IconButtonWhite = withStyles(theme => ({
  root: {
    backgroundColor: "transparent",
    padding: "5px"
  }
}))(IconButton);

/**
 * Small icon styling
 */

const IconSmall = withStyles(() => ({
  root: {
    fontSize: "1rem"
  }
}))(Icon);

/**
 * Layout 1 sidebar
 */

class Layout1Sidenav extends Component {
  state = {
    hidden: true
  };

  /**
   * Close sidebar on route change on mobile
   */

  componentDidMount() {
    // CLOSE SIDENAV ON ROUTE CHANGE ON MOBILE
    this.unlistenRouteChange = this.props.history.listen((location, action) => {
      if (isMdScreen()) {
        this.updateSidebarMode({ mode: "close" });
      }
    });

    setTimeout(() => {
      this.setState({ hidden: false });
    }, 400);
  }

  componentWillUnmount() {
    this.unlistenRouteChange();
  }

  /**
   * Sidebar update mode
   *
   * @param sidebarSettings
   */

  updateSidebarMode = sidebarSettings => {
    let { settings, setLayoutSettings, setDefaultSettings } = this.props;
    const updatedSettings = merge({}, settings, {
      layout1Settings: {
        leftSidebar: {
          ...sidebarSettings
        }
      }
    });

    setLayoutSettings(updatedSettings);
    setDefaultSettings(updatedSettings);
  };

  /**
   * Sidebar toggle
   * Is the bar permanently open or does it collapse
   */

  handleSidenavToggle = () => {
    let {
      settings: {
        layout1Settings: {
          leftSidebar: { mode }
        }
      }
    } = this.props;

    console.log(mode);

    this.updateSidebarMode({ mode: mode === "compact" ? "full" : "compact" });
  };

  /**
   * Open Brand component file to replace logo and text
   *
   * @returns {JSX.Element}
   */

  renderLogoSwitch = () => (
    <Brand>
      <Switch
        className="sidenav__toggle show-on-pc"
        onChange={this.handleSidenavToggle}
        checked={
          !(this.props.settings.layout1Settings.leftSidebar.mode === "full")
        }
        color="secondary"
      />
    </Brand>
  );

  /**
   * The UI representation being returned
   *
   * @returns {JSX.Element}
   */

  render() {
    let { theme, settings } = this.props;
    const sidenavTheme =
      settings.themes[settings.layout1Settings.leftSidebar.theme] || theme;
    return (
      <SidenavTheme theme={sidenavTheme} settings={settings}>
        <div className="sidenav">
          <div className="sidenav__hold" >
            {!this.state.hidden && (
              <Fragment>
                {this.renderLogoSwitch()}
                <Sidenav/>
              </Fragment>
            )}
          </div>
        </div>
      </SidenavTheme>
    );
  }
}

/**
 * Layout 1 sidebar properties
 */

Layout1Sidenav.propTypes = {
  setLayoutSettings: PropTypes.func.isRequired,
  setDefaultSettings: PropTypes.func.isRequired,
  settings: PropTypes.object.isRequired
};

/**
 * Mapping state to properties
 *
 * @param state
 */

const mapStateToProps = state => ({
  setDefaultSettings: PropTypes.func.isRequired,
  setLayoutSettings: PropTypes.func.isRequired,
  settings: state.layout.settings
});

/**
 * Export with styles
 */

export default withStyles(styles, { withTheme: true })(
  withRouter(
    connect(mapStateToProps, {
      setLayoutSettings,
      setDefaultSettings
    })(Layout1Sidenav)
  )
);
