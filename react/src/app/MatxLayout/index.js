import React from "react";

/**
 * Index for MatxLayouts
 * @type {{layout1: React.LazyExoticComponent<React.ComponentType<any>>}}
 */

export const MatxLayouts = {
  layout1: React.lazy(() => import("./Layout1/Layout1"))
};
