import "../styles/_app.scss";
import React from "react";
import { Provider } from "react-redux";
import { Router } from "react-router-dom";
import MatxTheme from "./MatxLayout/MatxTheme/MatxTheme";
import AppContext from "./appContext";
import history from "history.js";
import routes from "./RootRoutes";
import { Store } from "./redux/Store";
import MatxLayout from "./MatxLayout/MatxLayoutSFC";

/**
 * The core "wrapper" of the application
 *
 * MatxLaout -> Layout of the application
 *
 * @returns {JSX.Element}
 * @constructor
 */


const App = () => {
  return (
    <AppContext.Provider value={{ routes }}>
        <Provider store={Store}>
            <MatxTheme>
                <Router history={history}>
                    <MatxLayout />
                </Router>
            </MatxTheme>
        </Provider>
    </AppContext.Provider>
  );
};

export default App;
