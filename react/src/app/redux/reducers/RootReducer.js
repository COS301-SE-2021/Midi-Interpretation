import { combineReducers } from "redux";

import LayoutReducer from "./LayoutReducer";

import NotificationReducer from "./NotificationReducer";

import NavigationReducer from "./NavigationReducer";

const RootReducer = combineReducers({
  layout: LayoutReducer,
  notification: NotificationReducer,
  navigations: NavigationReducer
});

export default RootReducer;
