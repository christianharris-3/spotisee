import {Paper} from "@mui/material";
import UserAvatar from "../components/UserAvatar.jsx";
import {getHeaders } from "../utils/utils.js";
import {useNavigate} from "react-router-dom";

export default function Profile() {
    let loggedIn = false;
    let username = null;
    const navigate = useNavigate();

    if (localStorage.getItem("loggedIn") === "true") {
        loggedIn = true;
        username = localStorage.getItem("username")
    } else {
        navigate("/login")
    }

    fetch("/api/upload-data", {
        method: "GET",
        headers: getHeaders()
    }).then(r => r.json()).then(json => {
        console.log("json", json)
    });


    return (
        <div className="page">
            <div style={{paddingTop: "30px"}}>
                <div style={{textAlign: "center", width: "800px", margin: "auto", padding: "40px"}}>
                    <Paper style={{flexGrow: 1}}>
                        <UserAvatar username={username}/>
                        <span>{username}</span>
                    </Paper>
                </div>
            </div>
        </div>
    )
}