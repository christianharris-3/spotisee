import {getHeaders} from "../utils/utils.js";
import {Button, CircularProgress} from "@mui/material";
import {Delete} from "@mui/icons-material";
import {useState} from "react";

export default function DeleteUpload({uploadId, triggerDataReload}) {
    const [buttonLoading, setButtonLoading] = useState(false);

    function deleteUpload() {
        setButtonLoading(true)
        console.log("loading", uploadId)
        fetch("/api/upload-data/" + uploadId, {
            method: "DELETE", headers: getHeaders()
        }).then(r => {
            setButtonLoading(false)
            triggerDataReload()
        });
    }

    return (<div style={{width: "fit-content", margin: "auto"}}>
            {buttonLoading ?
            <div>
                <CircularProgress size="30px"/>
            </div> :
            <Button onClick={deleteUpload} style={{width: "fit-content", minWidth: "fit-content"}}>
                <Delete color="error"/>
            </Button>}
        </div>)
}