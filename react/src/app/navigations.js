import Cookies from "universal-cookie";

/**
 * The navigation items on the sidebar. Allows for navigations between the Upload and Display view.
 * @type {[{path: string, name: string, icon: string}, {path: string, name: string, icon: string}]}
 */
const cookies = new Cookies()
export const navigations = [
    {
        name: "Home",
        path: "/Home",
        icon: "home",
        enabled: true
    },
    {
        name: "About Us",
        path: "/About",
        icon: "personOutlined",
        enabled: true
    },
    {
        name: "Help",
        path: "/Help",
        icon: "book",
        enabled: true
    },
    {
        name: "Upload",
        path: "/Upload",
        icon: "cloud_Upload",
        enabled: true
    },
    (cookies.get('fileDesignator') !== undefined)?
        {
            name: "Display",
            path: "/Display",
            icon: "equalizer",
            enabled: true
            //badge: { value: "demo", color: "secondary" },
        }
    :
        {
            name: "Display",
            path: "/Display",
            icon: "equalizer",
            enabled: false
        }
];
