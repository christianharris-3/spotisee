import "./topbar.css";
import LogoBw from "../svg/LogoBw.jsx";
import {useNavigate} from "react-router-dom";
import {AppBar, Avatar, Box, Button, IconButton, Menu, MenuItem, SvgIcon, Toolbar, Typography} from "@mui/material";
import UserAvatar from "../UserAvatar.jsx";
import {useState} from "react";

export default function Topbar() {
    // const [profileMenuOpen, setProfileMenuOpen] = useState(false);
    // const [menuAnchor, setMenuAnchor] = useState(null);

    // const openMenu = (event) => {
    //     setMenuAnchor(event.currentTarget);
    // }

    let loggedIn = false;
    let username = null;
    const navigate = useNavigate();

    function loginButtonPress() {
        navigate("/login");
    }

    function profileButtonPress() {
        navigate("/profile");
    }

    if (localStorage.getItem("loggedIn") === "true") {
        loggedIn = true;
        username = localStorage.getItem("username")
    }

    return (
        <AppBar position="static" style={{height: 50}}>
            <div className="top-divider">
                <div style={{width: "20%", display: "flex", gap: 12, height: "100%", alignItems: "center"}}>
                    <LogoBw width={50} height={20} homeOnClick={true}/>
                    <span>Spotisee</span>
                </div>
                {loggedIn ?
                    <Box style={{display: "flex", alignItems: "center", gap: "8px"}}>
                        <span onClick={profileButtonPress}>{username}</span>
                        <UserAvatar username={username} onClick={profileButtonPress} style={{cursor: "pointer"}}/>
                    </Box>:
                    <Button variant="contained-primary" onClick={loginButtonPress} style={{}}>Sign In</Button>
                }
            </div>
        </AppBar>
    );
}