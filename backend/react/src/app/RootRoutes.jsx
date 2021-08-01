import React from "react";
import { Redirect } from "react-router-dom";

import uploadFileRoutes from "./views/UploadFileRoutes";

const redirectRoute = [
  {
    path: "/",
    exact: true,
    component: () => <Redirect to="/FileUpload" />
  }
];

const errorRoute = [
  {
    component: () => <Redirect to="/session/404" />
  }
];

const routes = [
  ...uploadFileRoutes,
  ...redirectRoute,
  ...errorRoute
];

export default routes;
