import React from "react";

const displayRoutes = [
  {
    path: "/Display",
    component: React.lazy(() => import("./Display")),
  }
];

export default displayRoutes;
