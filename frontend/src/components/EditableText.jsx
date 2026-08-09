import {Button, TextField} from "@mui/material";
import {useState} from "react";
import {getHeadersJson} from "../utils/utils.js";

export default function EditableText({row}) {
    const [showSaveButton, setShowSaveButton] = useState(false);
    const [saveButtonLoading, setSaveButtonLoading] = useState(false);
    const [newUploadName, setNewUploadName] = useState("");

    function uploadNameEdited(event, title) {
        setNewUploadName(title);
        if (title !== row.uploadName) {
            setShowSaveButton(true)
        } else {
            setShowSaveButton(false);
        }
    }

    function saveTitle(event, uploadId) {
        setSaveButtonLoading(true);
        fetch("/api/upload-data/"+uploadId, {
            method: "PUT",
            headers: getHeadersJson(),
            body: JSON.stringify({uploadName: newUploadName})
        }).then(r => {
            if (r.ok) {
                setSaveButtonLoading(false);
                setShowSaveButton(false);
                row.uploadName = newUploadName;
            } else {
                console.log("failed to edit title", r);
            }
        })
    }

    return (
        <div style={{display: "flex", gap: "3px", height: "29px"}}>
            <TextField variant="standard"
                       defaultValue={row.uploadName}
                       size="small"
                       style={{flexGrow: 1}}
                       onChange={(e) => {
                           uploadNameEdited(e, e.target.value, row.uploadId)
                       }}/>
            {showSaveButton ?
                <Button loading={saveButtonLoading} size="medium"
                    onClick={(e) => {saveTitle(e, row.uploadId)
                }}>Save</Button> : <></>
            }
        </div>
    )
}