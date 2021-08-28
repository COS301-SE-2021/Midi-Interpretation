import React from "react";

/**
 * Route of the Live Display page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const liveRoutes = [
    {
        path: "/Live",
        component: React.lazy(() => import("./Live")),
    }
];

export default liveRoutes;
