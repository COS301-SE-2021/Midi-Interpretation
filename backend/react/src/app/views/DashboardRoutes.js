import React from "react";

const dashboardRoutes = [
  {
    path: "/FileUpload",
    component: React.lazy(() => import("./FileUpload")),
  }
];

export default dashboardRoutes;
