import "./Topbar.css";
import LogoBw from "../Svg/LogoBw.jsx";
import {useNavigate} from "react-router-dom";
import {AppBar, Avatar, Box, Button, IconButton, Menu, MenuItem, SvgIcon, Toolbar, Typography} from "@mui/material";
import UserAvatar from "../UserAvatar.jsx";
import {useState} from "react";
import {getHeaders, getHeadersJson, logout} from "../../utils/utils.js";

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
        let authToken = localStorage.getItem("authToken");
        if (authToken === undefined || authToken === null) {
            return;
        }
        fetch("/api/auth", {
            method: "POST",
            headers: getHeadersJson(),
            body: JSON.stringify({token: authToken})}
        ).then(
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
                <div style={{display: "flex", gap: "50px"}}>
                    <div style={{minWidth: "100px", display: "flex", gap: 12, height: "100%", alignItems: "center"}}>
                        <LogoBw width={50} height={20} homeOnClick={true}/>
                        <span style={{fontWeight: "bold"}}>Spotisee</span>
                    </div>
                    <div style={{display: "flex", alignItems: "center", gap: "10px"}}>
                        <Button style={{color: "white"}} onClick={()=>navigate("/profile")}>Profile</Button>
                        <Button style={{color: "white"}} onClick={()=>navigate("/table")}>Table</Button>
                    </div>
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