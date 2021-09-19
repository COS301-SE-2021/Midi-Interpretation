import React from "react";

/**
 * Route of the About page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const aboutRoutes = [
    {
        path: "/About",
        component: React.lazy(() => import("./About")),
    }
];

export default aboutRoutes;
