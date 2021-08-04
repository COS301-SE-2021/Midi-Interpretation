import NotFound from "./NotFound";

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

const notFoundRoutes = [
  {
    path: "/404",
    component: NotFound,
    settings
  }
];

export default notFoundRoutes;
