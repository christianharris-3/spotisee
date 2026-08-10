import {Paper, TablePagination, ToggleButton, ToggleButtonGroup} from "@mui/material";
import {useEffect, useState} from "react";
import {getHeaders} from "../utils/utils.js";
import SearchBox from "../components/SearchBox/SearchBox.jsx";

export default function TablePage() {

    const [itemType, setItemType] = useState("songs");
    const [sortBy, setSortBy] = useState("totalMsPlayed");
    const [searchTerm, setSearchTerm] = useState("");

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

    useEffect(() => {
        const params = new URLSearchParams({
            searchTerm: searchTerm,
            pageSize: pageSize,
            pageIndex: currentPage-1,
            sortBy: sortBy
        });
        fetch(`/api/aggregate/${itemType}/${localStorage.getItem('activeUploadId')}?${params}`, {
            method: "GET",
            headers: getHeaders()
        })
            .then(r => r.json())
            .then(json => {
                console.log(json);
                setTableData(json);
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
            <div>Date Entry Here</div>
            <div>
                {tableData.map((object, key) => <div>{key} {object.artistName}</div>)}
            </div>
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