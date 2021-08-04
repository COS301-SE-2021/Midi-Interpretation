import "babel-polyfill";

import React from "react";
import ReactDOM from "react-dom";
import "./_index.scss";

import App from "./app/App";

// cssVars(); // for IE-11 support un-comment

ReactDOM.render(<App />, document.getElementById("root"));

