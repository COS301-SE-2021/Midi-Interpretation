import layout1Settings from "./Layout1/Layout1Settings";
import { themeColors } from "./MatxTheme/themeColors";
import { createMuiTheme } from "@material-ui/core/styles";
import { forEach, merge } from "lodash";
import themeOptions from "./MatxTheme/themeOptions";

/**
 * Settings to ensure colours and layouts are consistent throughout the system
 *
 * @returns {{}}
 */

function createMatxThemes() {
  let themes = {};

  forEach(themeColors, (value, key) => {
    themes[key] = createMuiTheme(merge({}, themeOptions, value));
  });
  return themes;
}
const themes = createMatxThemes();

/**
 * The application settings
 *
 * @type {{themes: {}, activeTheme: string, layout1Settings: {leftSidebar: {mode: string, bgImgURL: string, show: boolean, theme: string}, topbar: {show: boolean, fixed: boolean, theme: string}}, activeLayout: string, perfectScrollbar: boolean, secondarySidebar: {show: boolean, theme: string, open: boolean}}}
 */

export const MatxLayoutSettings = {
  activeLayout: "layout1",
  activeTheme: "purple1",
  perfectScrollbar: true,

  themes: themes,
  layout1Settings, // Layout1/Layout1Settings.js
};
