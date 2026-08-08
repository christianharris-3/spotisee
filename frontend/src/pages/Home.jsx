import {useNavigate} from "react-router-dom";
import {Alert, Button, Chip, CircularProgress, Paper, Typography} from "@mui/material";
import {useState} from "react";
import {AttachFile, UploadFile, Check} from "@mui/icons-material";


export default function Home() {
    const [zipFile, setZipFile] = useState(null);
    const [fileProcessing, setFileProcessing] = useState(false);
    const [fileProcessed, setFileProcessed] = useState(false);
    const [fileValid, setFileValid] = useState(null);

    const navigate = useNavigate();
    let loggedIn = false;


    if (localStorage.getItem("loggedIn") === "true") {
        loggedIn = true;
    }

    const handleFileChange = (event) => {
        setZipFile(event.target.files[0]);
    }
    const handleFileUpload = () => {
        const formData = new FormData();
        formData.set("file", zipFile);

        setFileProcessing(true);

        fetch("api/upload-data", {
            method: "POST",
            body: formData
        }).then(r => {
            setFileProcessed(true);
            if (r.ok) {
                setFileValid(true);
            } else {
                setFileValid(false);
            }
        })
    }

    const doneUpload = () => {
        setFileProcessed(false);
        setFileProcessing(false);
        setFileValid(null);
        setZipFile(null);
    }


    return (
        <div className="page">
            <div style={{paddingTop: "30px"}}>
                <Paper style={{textAlign: "center", width: "800px", margin: "auto", padding: "40px"}}>
                    <Typography variant="h2" style={{fontFamily: "Georgia", fontWeight: "bold", marginBottom: "30px"}}>
                        Spotisee
                    </Typography>
                    <Typography variant="body" style={{fontFamily: "Source Sans 3"}}>
                        All of the ways of looking at music listening data that I could come up with
                    </Typography>

                    {loggedIn ?
                        <div style={{margin: "20px"
                        }}>
                            {fileProcessed ?
                                <div>
                                    {fileValid ?
                                    <Alert severity="success" onClose={doneUpload} style={{width: "fit-content", margin: "auto"}}>
                                        Upload Complete
                                    </Alert> :
                                    <Alert severity="error" onClose={doneUpload} style={{width: "fit-content", margin: "auto"}}>
                                        Upload Failed
                                    </Alert>
                                    }
                                </div> :
                                <div>
                                    {fileProcessing ?
                                        <div style={{borderStyle: "solid", borderWidth: "1px", width: "fit-content", margin: "auto", padding: "10px", display: "flex", alignItems: "center", gap: "10px", borderRadius: "5px"}}>
                                            <CircularProgress></CircularProgress>
                                            <span>Uploading {zipFile.name}</span>
                                        </div> :
                                        <div>
                                            <Button variant="contained"
                                                    component="label"
                                                    style={{marginTop: "10px"}}
                                                    startIcon={<AttachFile />}>
                                                Select File
                                                <input hidden type="file" accept=".zip" onChange={handleFileChange}/>
                                            </Button>
                                            <div style={{padding: "10px"}}>
                                                {(zipFile === null || zipFile === undefined) ?
                                                <></> :
                                                <div style={{marginTop: "15px", gap: "10px", display: "flex", alignItems: "center", justifyContent: "center"}}>
                                                    <Chip label={zipFile.name} color="default" onDelete={() => setZipFile(null)}/>
                                                    <Button variant="outlined" startIcon={<UploadFile />} onClick={handleFileUpload}>Upload</Button>
                                                </div>
                                                }
                                            </div>
                                        </div>
                                    }
                                </div>
                            }

                        </div> :
                        <div style={{margin: "20px"}}>
                            <Button variant="contained"
                                    style={{margin: "10px"}}
                                    onClick={() => {
                                        navigate("/login")
                                    }}>
                                Sign In
                            </Button>
                            <Button variant="outlined"
                                    style={{margin: "10px"}}
                                    onClick={() => {
                                        navigate("/register")
                                    }}>
                                Register
                            </Button>
                        </div>
                    }
                </Paper>
            </div>
        </div>
    )
}