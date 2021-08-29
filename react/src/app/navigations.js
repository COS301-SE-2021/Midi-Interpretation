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
    {
        name: "Analysis",
        path: "/Upload",
        icon: "timeline",
        enabled: true,
        children:[
            {
                name: "Live Analysis",
                path: "/Live",
                icon: "open_in_browser",
                enabled: true
            },
            // disable the Display view if no file has been processed
            (cookies.get('fileDesignator') !== undefined)?
                {
                    name: "Display",
                    path: "/Display",
                    icon: "equalizer",
                    enabled: true
                }
                :
                {
                    enabled: false,
                    name: "Display",
                    path: "/Display",
                    icon: "equalizer"
                }
        ]
    }
];
