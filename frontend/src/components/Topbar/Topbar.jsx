import "./topbar.css";
import LogoBw from "../svg/LogoBw.jsx";
import {useNavigate} from "react-router-dom";
import {AppBar, Button, IconButton, SvgIcon, Toolbar} from "@mui/material";

export default function Topbar() {
    let loggedIn = false;
    const navigate = useNavigate();

    async function loginButtonPress() {
        navigate("/login");
    }

    if (localStorage.getItem("loggedIn") === "true") {
        loggedIn = true;
    }

    return (
        <AppBar position="static" style={{height: 50}}>
            <div className="top-divider">
                <div style={{width: "20%", display: "flex", gap: 12, height: "100%", alignItems: "center"}}>
                    <LogoBw width={50} height={20} homeOnClick={true}/>
                    <span>Spotisee</span>
                </div>
                {loggedIn ?
                    <Button variant="contained-primary" onClick={loginButtonPress} style={{}}>Sign In</Button> :
                    <div>signed in</div>
                }
            </div>
        </AppBar>
    );
}