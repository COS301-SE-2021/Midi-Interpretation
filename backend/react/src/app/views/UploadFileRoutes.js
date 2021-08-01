import React from "react";

const uploadFileRoutes = [
  {
    path: "/FileUpload",
    component: React.lazy(() => import("./FileUpload")),
  }
];

export default uploadFileRoutes;
