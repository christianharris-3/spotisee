import "./topbar.css";
import LogoBw from "../svg/LogoBw.jsx";
import {useNavigate} from "react-router-dom";
import {AppBar, Avatar, Box, Button, IconButton, SvgIcon, Toolbar, Typography} from "@mui/material";

export default function Topbar() {
    let loggedIn = false;
    let username = null;
    const navigate = useNavigate();

    async function loginButtonPress() {
        navigate("/login");
    }

    if (localStorage.getItem("loggedIn") === "true") {
        loggedIn = true;
        username = localStorage.getItem("username")
    }
    console.log("username: ", username);

    return (
        <AppBar position="static" style={{height: 50}}>
            <div className="top-divider">
                <div style={{width: "20%", display: "flex", gap: 12, height: "100%", alignItems: "center"}}>
                    <LogoBw width={50} height={20} homeOnClick={true}/>
                    <span>Spotisee</span>
                </div>
                {loggedIn ?
                    <Box style={{display: "flex", alignItems: "center", gap: "8px"}}>
                        <span>{username}</span>
                        <Avatar alt={username}/>
                    </Box>:
                    <Button variant="contained-primary" onClick={loginButtonPress} style={{}}>Sign In</Button>
                }
            </div>
        </AppBar>
    );
}