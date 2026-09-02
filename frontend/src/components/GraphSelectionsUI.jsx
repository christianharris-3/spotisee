import {Button, CircularProgress, Dialog, FormControl, InputLabel, MenuItem, Select} from "@mui/material";
import {useEffect, useState} from "react";
import {getHeaders, getHeadersJson, validateResponse} from "../utils/utils.js";
import {useNavigate} from "react-router-dom";
import EditableText from "./EditableText.jsx";


export default function GraphSelectionsUI({isOpen, setIsOpen}) {
    const navigate = useNavigate();

    const [selectionsList, setSelectionsList] = useState(null);
    const [currentSelection, setCurrentSelection] = useState(null);

    function handleChangeGraphSelection(event) {
        setCurrentSelection(
            selectionsList.filter(item => item.selectionTitle === event.target.value).at(0)
        )
    }

    useEffect(() => {
        fetch("/api/selection", {
            method: "GET",
            headers: getHeaders()
        }).then(r => {
            if (validateResponse(r, navigate)) {
                r.json().then(json => {
                    console.log(json)
                    setSelectionsList(json)
                    if (currentSelection === null) {
                        setCurrentSelection(json[0]);
                    }
                })
            }
        })
    }, []);

    function saveTitleText(newSelectionTitle, row) {
        return fetch("/api/selection/"+row.uploadId, {
            method: "PUT",
            headers: getHeadersJson(),
            body: JSON.stringify({selectionTitle: newSelectionTitle})
        }).then(r => {
            if (r.ok) {
                row.selectionTitle = newSelectionTitle;
                return true
            }
            return false;
        })
    }

    return (
        <Dialog open={isOpen}>
            {selectionsList === null ?
                <CircularProgress /> :
                <div>
                    <FormControl variant="outlined" size="small" style={{minWidth: "150px"}}>
                        <InputLabel>Graph</InputLabel>
                        <Select variant="outlined"
                                value={currentSelection.selectionTitle}
                                label="Graph"
                                labelId="graphSelectionLabel"
                                onChange={handleChangeGraphSelection}
                        >
                            {selectionsList.map((item) =>
                                <MenuItem value={item.selectionTitle}>
                                    {item.selectionTitle}
                                </MenuItem>
                            )}
                        </Select>
                    </FormControl>
                </div>
            }
            <Button onClick={()=>setIsOpen(false)}>close</Button>
            {currentSelection !== null &&
                <div>
                    <EditableText
                        defaultText={currentSelection.selectionTitle}
                        row={currentSelection}
                        saveFunc={saveTitleText}
                    />
                </div>
            }
        </Dialog>
    )
}