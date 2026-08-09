import {Alert, Button, Chip, CircularProgress, Typography} from "@mui/material";
import {useState} from "react";
import {AttachFile, UploadFile} from "@mui/icons-material";
import {getHeaders} from "../utils/utils.js";
import {useDropzone} from "react-dropzone";

export default function UploadSection({triggerDataReload, ...props}) {
    const [zipFile, setZipFile] = useState(null);
    const [fileProcessing, setFileProcessing] = useState(false);
    const [fileProcessed, setFileProcessed] = useState(false);
    const [fileValid, setFileValid] = useState(null);


    const {getRootProps, getInputProps} = useDropzone({
        accept: {
            "application/zip": [".zip"]
        },
        noClick: (zipFile !== null),
        onDrop: acceptedFiles => {
            if (acceptedFiles.length > 0) {
                setZipFile(acceptedFiles.at(0))
            }
        }
    });

    function selectFilePressed(event) {
        if (zipFile === null) {
            event.preventDefault();
        }
    }

    const handleFileUpload = (e) => {
        e.preventDefault()
        const formData = new FormData();
        formData.set("file", zipFile);

        setFileProcessing(true);

        fetch("api/upload-data", {
            method: "POST",
            body: formData,
            headers: getHeaders()
        }).then(r => {
            setFileProcessed(true);
            if (r.ok) {
                setFileValid(true);
                triggerDataReload()
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
        <div {...props} style={{margin: "20px"}}>
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
                        <div {...getRootProps()} style={{borderStyle: "dashed", borderRadius: 3, borderWidth: 3}}>
                            <Button variant="contained"
                                    component="label"
                                    style={{marginTop: "10px", margin: "20px"}}
                                    startIcon={<AttachFile />}
                                    onClick={selectFilePressed}>
                                Select File
                                <input hidden type="file" {...getInputProps()}/>
                            </Button>
                            <Typography sx={{color: "secondary"}}>Attach your <i><b>my_spotify_data.zip</b></i> file</Typography>
                            <div style={{paddingBottom: "20px"}}>
                                {(zipFile === null || zipFile === undefined) ?
                                    <></> :
                                    <div style={{marginTop: "15px", gap: "10px", display: "flex", alignItems: "center", justifyContent: "center"}}>
                                        <Chip label={zipFile.name} color="default" onDelete={() => setZipFile(null)}/>
                                        <Button variant="outlined" startIcon={<UploadFile />} onClick={handleFileUpload} >Upload</Button>
                                    </div>
                                }
                            </div>
                        </div>
                    }
                </div>
            }
        </div>
    )
}