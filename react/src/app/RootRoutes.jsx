import React from "react";
import { Redirect } from "react-router-dom";
import homeRoutes from "./views/HomeRoutes";
import uploadRoutes from "./views/UploadRoutes";
import displayRoutes from "./views/DisplayRoutes";
import notFoundRoutes from "./views/NotFoundRoutes";

/**
 * The routing information of the system
 */

/**
 * The default path of the application. Redirects to the Upload view when the application is opened.
 * @type {[{path: string, component: (function()), exact: boolean}]}
 */

const redirectRoute = [
  {
    path: "/",
    exact: true,
    component: () => <Redirect to="/Home" />
  }
];

/**
 * Error route of the application. When the system tries to navigate to an invalid path, it will be sent to a 404 view instead
 * @type {[{component: (function())}]}
 */

const errorRoute = [
  {
    component: () => <Redirect to="/404" />
  }
];

/**
 * The mapping of other route files to allow our system to navigate to those views
 */

const routes = [
  ...homeRoutes,
  ...uploadRoutes,
  ...displayRoutes,
  ...notFoundRoutes,
  ...redirectRoute,
  ...errorRoute
];

export default routes;
