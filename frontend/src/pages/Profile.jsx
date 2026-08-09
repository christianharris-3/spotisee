import {Avatar, Button, Divider, Paper, Typography} from "@mui/material";
import UserAvatar from "../components/UserAvatar.jsx";
import {getHeaders, logout} from "../utils/utils.js";
import {useNavigate} from "react-router-dom";
import {UploadFile} from "@mui/icons-material";
import UploadSection from "../components/UploadSection.jsx";

export default function Profile() {
    let username = null;
    const navigate = useNavigate();

    if (localStorage.getItem("loggedIn") === "true") {
        username = localStorage.getItem("username")
    } else {
        navigate("/login")
    }

    function logoutPressed() {
        logout()
        navigate("/")
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
                <div style={{textAlign: "center", maxWidth: "800px", margin: "auto", padding: "40px"}}>
                    <Paper style={{flexGrow: 1, display: "flex", padding: "30px"}}>
                        <UserAvatar username={username} sx={{width: 200, height: 200, fontSize: 80, marginRight: "40px"}}/>
                        <Divider orientation="vertical" flexItem/>
                        <div style={{padding: "30px"}}>
                            <Typography variant="h3">Welcome, {username}</Typography>
                            <Typography variant="body">No, I'm not going to let you upload a profile picture, I haven't figured out how to store that yet.</Typography>
                            <br></br>
                            <Button variant="outlined" style={{marginTop: "20px"}} onClick={logoutPressed}>Logout</Button>
                        </div>
                    </Paper>
                    <div style={{paddingTop: "30px"}}>
                        <Paper style={{padding: "10px"}}>
                            <Typography sx={{margin: "20px"}} variant="h5" >Upload Your Spotify Data</Typography>
                            <Typography variant="body" style={{marginTop: "10px"}}>
                                To download your data, go to
                                this <a target="_blank" href="https://www.spotify.com/us/account/privacy/">Spotify page</a> and
                                select "Extended Streaming History", on request, within a few weeks you will be emailed
                                your <i><b>my_spotify_data.zip</b></i>
                            </Typography>
                            <UploadSection style={{}}/>
                        </Paper>
                    </div>
                    <div style={{paddingTop: "30px"}}>
                        <Paper style={{padding: "10px"}}>
                            yo hi
                        </Paper>
                    </div>
                </div>
            </div>
        </div>
    )
}