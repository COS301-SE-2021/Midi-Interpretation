/**
 * The navigation items on the sidebar. Allows for navigations between the Upload and Display view.
 * @type {[{path: string, name: string, icon: string}, {path: string, name: string, icon: string}]}
 */

export const navigations = [
    {
        name: "Home",
        path: "/Home",
        icon: "home"
    },
    {
        name: "About Us",
        path: "/About",
        icon: "priority_high"
    },
    {
        name: "Upload",
        path: "/Upload",
        icon: "cloud_upload"
        // badge: { value: "demo", color: "secondary" },
    }
];
