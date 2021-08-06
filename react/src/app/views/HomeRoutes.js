import React from "react";

/**
 * Route of the Welcome page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const homeRoutes = [
    {
        path: "/Home",
        component: React.lazy(() => import("./Home")),
    }
];

export default homeRoutes;
