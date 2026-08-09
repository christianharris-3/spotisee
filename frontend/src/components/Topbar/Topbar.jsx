import "./topbar.css";
import LogoBw from "../svg/LogoBw.jsx";
import {useNavigate} from "react-router-dom";
import {AppBar, Avatar, Box, Button, IconButton, Menu, MenuItem, SvgIcon, Toolbar, Typography} from "@mui/material";
import UserAvatar from "../UserAvatar.jsx";
import {useState} from "react";
import {getHeaders, logout} from "../../utils/utils.js";

export default function Topbar() {
    const [loggedIn, setLoggedIn] = useState(false);
    let username = null;
    const navigate = useNavigate();

    function loginButtonPress() {
        navigate("/login");
    }


    function profileButtonPress() {
        navigate("/profile");
    }

    function lifeLine() {
        fetch("/api/auth", {method: "GET", headers: getHeaders()}).then(
            r => {
                if (r.ok) {
                    r.json().then(
                        json => {
                            localStorage.setItem("username", json["username"]);
                            localStorage.setItem("activeUploadId", json["activeUploadId"])
                            setLoggedIn(true);
                        });
                } else {
                    setLoggedIn(false);
                    logout();
                }
            }
        )
    }
    username = localStorage.getItem("username")
    lifeLine()

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
                        <UserAvatar username={username} onClick={profileButtonPress} sx={{cursor: "pointer"}}/>
                    </Box>:
                    <Button variant="contained-primary" onClick={loginButtonPress} style={{}}>Sign In</Button>
                }
            </div>
        </AppBar>
    );
}