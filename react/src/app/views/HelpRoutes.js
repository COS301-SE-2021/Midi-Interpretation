import React from "react";

/**
 * Route of the Help page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const helpRoutes = [
    {
        path: "/Help",
        component: React.lazy(() => import("./Help")),
    }
];

export default helpRoutes;
