const textLight = {
  primary: "rgba(52, 49, 76, 1)",
  secondary: "rgba(52, 49, 76, 0.54)",
  disabled: "rgba(52, 49, 76, 0.38)",
  hint: "rgba(52, 49, 76, 0.38)"
};
const secondaryColor = {
  light: "#90fad2",
  main: "#12d4fc",
  dark: "#0dbff6",
  contrastText: textLight.primary
};

export const themeColors = {
  white: {
    palette: {
      type: "light",
      primary: {
        main: "#ffffff",
        contrastText: textLight.primary
      },
      secondary: {
        main: "#387dd6",
        contrastText: textLight.primary
      },
      text: textLight
    }
  },
  slateDark1: {
    palette: {
      type: "dark",
      primary: {
        main: "#222A45",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#0dbff6",
        contrastText: textLight.primary
      },
      background: {
        paper: "#222A45",
        default: "#1a2038"
      }
    }
  },
  slateDark2: {
    palette: {
      type: "dark",
      primary: {
        main: "#1a2038",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#12d4fc",
        contrastText: textLight.primary
      },
      background: {
        paper: "#222A45",
        default: "#1a2038"
      }
    }
  },
  purple1: {
    palette: {
      type: "light",
      primary: {
        main: "#387dd6",
        contrastText: "#ffffff"
      },
      secondary: secondaryColor,
      text: textLight
    }
  },
  purple2: {
    palette: {
      type: "light",
      primary: {
        main: "#387dd6",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#12d4fc",
        contrastText: textLight.primary
      },
      text: textLight
    }
  },
  purpleDark1: {
    palette: {
      type: "dark",
      primary: {
        main: "#387dd6",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#12d4fc",
        contrastText: textLight.primary
      },
      background: {
        paper: "#222A45",
        default: "#1a2038"
      }
    }
  },
  purpleDark2: {
    palette: {
      type: "dark",
      primary: {
        main: "#387dd6",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#12d4fc",
        contrastText: textLight.primary
      },
      background: {
        paper: "#222A45",
        default: "#1a2038"
      }
    }
  },
  blue: {
    palette: {
      type: "light",
      primary: {
        main: "#387dd6",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#12d4fc",
        contrastText: textLight.primary
      },
      text: textLight
    }
  },
  blueDark: {
    palette: {
      type: "dark",
      primary: {
        main: "#387dd6",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#FF4F30",
        contrastText: textLight.primary
      },
      background: {
        paper: "#222A45",
        default: "#1a2038"
      }
    }
  },
  red: {
    palette: {
      type: "dark",
      primary: {
        main: "#e53935",
        contrastText: "#ffffff"
      },
      secondary: {
        main: "#12d4fc",
        contrastText: textLight.primary
      }
    }
  }
};
