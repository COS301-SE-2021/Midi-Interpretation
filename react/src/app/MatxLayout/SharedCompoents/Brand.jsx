import React from "react";

/**
 * Default branding information
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */

const Brand = ({ children }) => {
  return (
    <div className="flex items-center justify-between brand-area">
      <div className="flex items-center brand">
        <img src= {process.env.PUBLIC_URL + "/assets/images/logo.png"} alt="MIDISense" />
        <span className="brand__text">MidiSense</span>
      </div>
      {children}
    </div>
  );
};

export default Brand;
