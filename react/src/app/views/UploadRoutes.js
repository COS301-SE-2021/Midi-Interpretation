import React from "react";

/**
 * Route of the Upload page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const uploadRoutes = [
  {
    path: "/Upload",
    component: React.lazy(() => import("./Upload")),
  },
  {
    path: "/Loading",
    component: React.lazy(() => import("./Home")),
  }
];

export default uploadRoutes;
