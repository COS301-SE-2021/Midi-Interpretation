import React from "react";
import { Redirect } from "react-router-dom";


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
  ...redirectRoute,
  ...errorRoute
];

export default routes;
