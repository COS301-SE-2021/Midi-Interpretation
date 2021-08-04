import React from "react";

/**
 * Route of the Display page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const displayRoutes = [
  {
    path: "/Display",
    component: React.lazy(() => import("./Display")),
  }
];

export default displayRoutes;
