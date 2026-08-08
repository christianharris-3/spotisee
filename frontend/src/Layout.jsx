import Topbar from "./components/Topbar/Topbar.jsx";
import {Outlet } from "react-router-dom";
import "./styles/globals.css";
import {ThemeProvider} from "@mui/material/styles";
import {createTheme} from "@mui/material";

export default function Layout() {

    const theme = createTheme({
        palette: {
            mode: 'light',
            primary: {
                main: '#9c8939',
                light: "#af9a41",
                dark: "#877631",
                contrastText: "#ffffff"
            },
            secondary: {
                main: '#9c5a0b',
            },
            success: {
                main: '#cddc39',
            },
            info: {
                main: '#afb42b',
            },
            background: {
                default: '#ffffff',
                paper: '#f7f7f7',
            },
        },
        // components: {
        //     MuiButtonBase: {
        //         defaultProps: {
        //             disableRipple: true,
        //         }
        //     }
        // }
    });

    return (
        <ThemeProvider theme={theme}>
            <Topbar />
            <main className="page-container">
                <Outlet />
            </main>
        </ThemeProvider>
    )
}