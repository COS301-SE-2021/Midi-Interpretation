import React from "react";

const uploadRoutes = [
  {
    path: "/Upload",
    component: React.lazy(() => import("./Upload")),
  }
];

export default uploadRoutes;
