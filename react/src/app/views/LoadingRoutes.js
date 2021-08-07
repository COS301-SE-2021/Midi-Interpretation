import Loading from "./Loading";

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
 * Route of the 404 page
 */

const loadingRoutes = [
    {
        path: "/Loading",
        component: Loading,
        settings
    }
];

export default loadingRoutes;
