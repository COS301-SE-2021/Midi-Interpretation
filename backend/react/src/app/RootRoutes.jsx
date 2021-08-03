import React from "react";
import { Redirect } from "react-router-dom";

import uploadRoutes from "./views/UploadRoutes";
import displayRoutes from "./views/DisplayRoutes";

/**
 * TODO: commenting and refining
 * @type {[{path: string, component: (function()), exact: boolean}]}
 */

const redirectRoute = [
  {
    path: "/",
    exact: true,
    component: () => <Redirect to="/Upload" />
  }
];

const errorRoute = [
  {
    component: () => <Redirect to="/session/404" />
  }
];

const routes = [
  ...uploadRoutes,
  ...displayRoutes,
  ...redirectRoute,
  ...errorRoute
];

export default routes;
