import React from "react";

/**
 * Theme settings
 * @type {{layout1Settings: {leftSidebar: {mode: string, show: boolean}}, activeLayout: string}}
 */

const settings = {
    activeLayout: "layout1",
    layout1Settings: {
        leftSidebar: {
            show: false,
            mode: "close"
        }
    }
};


/**
 * Route of the Welcome page
 * @type {[{path: string, component: React.LazyExoticComponent<React.ComponentType<any>>}]}
 */

const homeRoutes = [
    {
        path: "/Home",
        component: React.lazy(() => import("./Home")),
        settings
    }
];

export default homeRoutes;
