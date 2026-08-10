import "./SearchBox.css";
import {TextField} from "@mui/material";
import {Search} from "@mui/icons-material";


export default function SearchBox({setSearchText}) {

    const handleChange = (event) => {
        setSearchText(event.target.value)
    }

    return (
        <div className="searchBox">
            <Search />
            <input type="text" onChange={handleChange}>

            </input>
        </div>
    )
}