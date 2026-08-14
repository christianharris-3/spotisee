import "./SearchBox.css";
import {TextField} from "@mui/material";
import {Clear, Search} from "@mui/icons-material";
import {useRef} from "react";


export default function SearchBox({setSearchText}) {

    const inputRef = useRef(null);

    const handleChange = (event) => {
        setSearchText(event.target.value)
    }

    const clearText = () => {
        setSearchText("")
        inputRef.current.value = "";
    }

    return (
        <div className="searchBox">
            <Search />
            <input type="text" ref={inputRef} onChange={handleChange} />

            <div>
                <Clear onClick={clearText} className="clearButton"/>
            </div>
        </div>
    )
}