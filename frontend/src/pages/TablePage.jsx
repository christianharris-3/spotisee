import {Paper, Stack, TablePagination, ToggleButton, ToggleButtonGroup} from "@mui/material";
import {useEffect, useState} from "react";
import {getHeaders} from "../utils/utils.js";
import SearchBox from "../components/SearchBox/SearchBox.jsx";
import Selector from "../components/Selector/Selector.jsx";

export default function TablePage() {

    const [itemType, setItemType] = useState("songs");
    const [sortBy, setSortBy] = useState("totalMsPlayed");
    const [searchTerm, setSearchTerm] = useState("");

    const [dateTypeSelection, setDateTypeSelection] = useState("All");

    const [yearSelectionOptions, setYearSelectionOptions] = useState([]);
    const [yearSelection, setYearSelection] = useState(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(100);

    const [tableData, setTableData] = useState([]);


    let handleMovePage = (event, newPage) => {
        setCurrentPage(newPage);
    }
    let handleChangePageSize = (event) => {
        setCurrentPage(0);
        setPageSize(parseInt(event.target.value, 10))
    }

    const getUploadId = () => {
        return localStorage.getItem('activeUploadId')
    }

    useEffect(() => {
        const params = new URLSearchParams({
            searchTerm: searchTerm,
            pageSize: pageSize,
            pageIndex: currentPage,
            sortBy: sortBy
        });
        fetch(`/api/aggregate/${itemType}/${getUploadId()}?${params}`, {
            method: "GET",
            headers: getHeaders()
        })
            .then(r => r.json())
            .then(json => {
                console.log(json);
                setTableData(json);
            })
        fetch(`/api/upload-data/${getUploadId()}`, {
            method: "GET",
            headers: getHeaders()
        }).then(r => {
            if (r.ok) {
                r.json().then(
                    json => {
                        console.log(json)
                    }
                )
            } else {
                console.log("ERROR: upload data info failed to load, id: ", getUploadId())
            }
        })


    }, [itemType, sortBy, searchTerm, pageSize, currentPage]);


    return (
        <div className="page">
            <div style={{paddingTop: "30px", display: "flex", gap: "10px"}}>
                <SearchBox setSearchText={setSearchTerm}/>
                <ToggleButtonGroup
                    size="small"
                    value={itemType}
                    exclusive
                    onChange={(e, value) => {
                        if (value !== null) setItemType(value)
                    }}>
                    <ToggleButton value="songs">Songs</ToggleButton>
                    <ToggleButton value="albums">Albums</ToggleButton>
                    <ToggleButton value="artists">Artists</ToggleButton>
                    <ToggleButton value="all">Combined</ToggleButton>
                </ToggleButtonGroup>
                <ToggleButtonGroup
                    size="small"
                    value={sortBy}
                    exclusive
                    onChange={(e, value) => {
                        if (value !== null) setSortBy(value)
                    }}>
                    <ToggleButton value="totalMsPlayed">Listen Time</ToggleButton>
                    <ToggleButton value="listens">Total Listens</ToggleButton>
                </ToggleButtonGroup>
            </div>
            <Stack style={{alignItems: "center"}}>
                <div style={{minWidth: "400px", width: "30%", padding: "12px"}}>
                    <Selector
                        items={["All", "Year", "Month", "Custom"]}
                        selectedValue={dateTypeSelection}
                        setSelectedValue={setDateTypeSelection}
                    />
                </div>
                <div>
                    {dateTypeSelection === "Year" || dateTypeSelection === "Month" ?
                        <Selector
                            style={{minWidth: "400px"}}
                            items={yearSelectionOptions}
                            selectedValue={yearSelection}
                            setSelectedValue={setYearSelection}
                        /> :
                        <div>
                        {dateTypeSelection === "Custom" ?
                            <div>
                                <input type="date"></input>
                            </div> : <></>
                        }
                        </div>
                    }
                </div>
            </Stack>
            {/*<div>*/}
            {/*    {tableData.map((object, key) => <div>{key} {object.artistName}</div>)}*/}
            {/*</div>*/}
            <div style={{display: "flex", justifyContent: "center"}}>
                <TablePagination
                    sx={{".MuiTablePagination-displayedRows": {minWidth: "150px"}}}
                    count={10000}
                    onPageChange={handleMovePage}
                    page={currentPage}
                    rowsPerPage={pageSize}
                    rowsPerPageOptions={[20, 50, 100]}
                    showFirstButton={true}
                    showLastButton={true}
                    onRowsPerPageChange={handleChangePageSize}/>
            </div>
        </div>
    )
}