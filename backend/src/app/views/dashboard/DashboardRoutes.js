import React from "react";
import { authRoles } from "../../auth/authRoles";

const dashboardRoutes = [
  {
    path: "/dashboard/analytics",
    component: React.lazy(() => import("./FileUpload")),
    auth: authRoles.admin
  }
];

export default dashboardRoutes;
