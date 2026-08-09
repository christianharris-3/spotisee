import {
    Button, Checkbox,
    Divider,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead, TableRow, TextField,
    Typography
} from "@mui/material";
import UserAvatar from "../components/UserAvatar.jsx";
import {getHeaders, logout} from "../utils/utils.js";
import {useNavigate} from "react-router-dom";
import UploadSection from "../components/UploadSection.jsx";
import {useEffect, useState} from "react";
import EditableText from "../components/EditableText.jsx";
import {Delete} from "@mui/icons-material";
import DeleteUpload from "../components/DeleteUpload.jsx";

export default function Profile() {
    const navigate = useNavigate();
    const [uploadItems, setUploadItems] = useState(null);
    const [triggerDataReload, setTriggerDataReload] = useState(1);
    const [selectedUpload, setSelectedUpload] = useState(null);

    useEffect(() => {
        if (localStorage.getItem("loggedIn") !== "true") {
            navigate("/login")
        }
    })

    function logoutPressed() {
        logout()
        navigate("/")
    }

    useEffect(() => {
        fetch("/api/upload-data", {
            method: "GET",
            headers: getHeaders()
        }).then(r => r.json()).then(json => {
            if (json.length > 0) {
                setSelectedUpload(json[0].uploadId);
            }
            setUploadItems(Object.fromEntries(
                json.map(item => [item.uploadId, item])
            ));
        });
    }, [triggerDataReload]);

    useEffect(() => {
        if (selectedUpload !== null) {
            localStorage.setItem("activeUploadId", selectedUpload.toString())
            fetch("/api/upload-data/select/"+selectedUpload, {
                method: "POST",
                headers: getHeaders()
            }).then()
        }
    }, [selectedUpload]);

    function runTriggerDataReload() {
        setTriggerDataReload(triggerDataReload + 1);
    }

    function formatDate(dateList) {
        if (dateList === null) {
            return "Unknown"
        }
        let date = new Date(...dateList);
        let month = date.toLocaleString("en-gb", {month: "short"});
        return `${month} ${date.getFullYear()}`
    }

    function uploadSelected(uploadId) {
        setSelectedUpload(uploadId);
    }



    return (
        <div className="page">
            <div style={{paddingTop: "30px"}}>
                <div style={{textAlign: "center", maxWidth: "800px", margin: "auto", padding: "40px"}}>
                    <Paper style={{flexGrow: 1, display: "flex", padding: "30px"}}>
                        <UserAvatar username={localStorage.getItem("username")} sx={{width: 200, height: 200, fontSize: 80, marginRight: "40px"}}/>
                        <Divider orientation="vertical" flexItem/>
                        <div style={{padding: "30px"}}>
                            <Typography variant="h3">Welcome, {localStorage.getItem("username")}</Typography>
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
                            <UploadSection triggerDataReload={runTriggerDataReload}/>
                        </Paper>
                    </div>
                    <div style={{paddingTop: "30px"}}>
                        <Paper style={{padding: "10px"}}>
                            <Typography variant="h5">Your Uploads</Typography>
                            {uploadItems === null ?
                                <div>Loading...</div>:
                            <TableContainer>
                                <Table>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Active</TableCell>
                                            <TableCell>Name</TableCell>
                                            <TableCell>Items</TableCell>
                                            <TableCell>Start</TableCell>
                                            <TableCell>End</TableCell>
                                            <TableCell>Delete</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {Object.values(uploadItems).map((row) => (
                                            <TableRow hover key={row.uploadId}>
                                                <TableCell>
                                                    <Checkbox checked={selectedUpload === row.uploadId} onClick={() => {uploadSelected(row.uploadId)}} size="large"/>
                                                </TableCell>
                                                <TableCell sx={{padding: "1px", paddingTop: "8px", width: "270px"}}>
                                                    <EditableText row={row} triggerDataReload={runTriggerDataReload}/>
                                                </TableCell>
                                                <TableCell>{row.itemCount}</TableCell>
                                                <TableCell>{formatDate(row.startDate)}</TableCell>
                                                <TableCell>{formatDate(row.endDate)}</TableCell>
                                                <TableCell sx={{width: "50px"}}>
                                                    <DeleteUpload uploadId={row.uploadId} triggerDataReload={runTriggerDataReload}/>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                            }
                        </Paper>
                    </div>
                </div>
            </div>
        </div>
    )
}