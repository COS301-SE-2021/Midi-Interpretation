/**
 * External from MIDISense
 */

import { combineReducers } from "redux";

import LayoutReducer from "./LayoutReducer";

import NavigationReducer from "./NavigationReducer";

const RootReducer = combineReducers({
  layout: LayoutReducer,
  navigations: NavigationReducer
});

export default RootReducer;
