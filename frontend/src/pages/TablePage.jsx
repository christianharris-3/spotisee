import {Paper, Stack, TablePagination, ToggleButton, ToggleButtonGroup} from "@mui/material";
import {useEffect, useRef, useState} from "react";
import {getHeaders, getUploadId, toDateString} from "../utils/utils.js";
import SearchBox from "../components/SearchBox/SearchBox.jsx";
import {DateSelector} from "../components/DateSelector.jsx";
import SongDataTable from "../components/SongDataTable/SongDataTable.jsx";

export default function TablePage() {

    const [itemType, setItemType] = useState("songs");
    const [sortBy, setSortBy] = useState("totalMsPlayed");
    const [searchTerm, setSearchTerm] = useState("");

    const [startDate, setStartDate] = useState(new Date(2000, 0));
    const [endDate, setEndDate] = useState(new Date(2040, 0));

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(20);

    const [tableData, setTableData] = useState([]);

    const dateSelector = useRef(null);


    const handleMovePage = (event, newPage) => {
        setCurrentPage(newPage);
    }
    const handleChangePageSize = (event) => {
        setCurrentPage(0);
        setPageSize(parseInt(event.target.value, 10))
    }

    const updateSearchTerm = (newSearchTerm) => {
        setSearchTerm(newSearchTerm);
        setCurrentPage(0);
        dateSelector.current?.updateDatesAvailable(newSearchTerm, itemType);
    }
    const updateItemType = (newItemType) => {
        setItemType(newItemType);
        setCurrentPage(0);
        dateSelector.current?.updateDatesAvailable(searchTerm, newItemType);
    }
    useEffect(() => {
        dateSelector.current?.updateDatesAvailable(searchTerm, itemType);
    }, [])


    // Load Table Data
    useEffect(() => {
        const params = new URLSearchParams({
            searchTerm: searchTerm,
            start: toDateString(startDate),
            end: toDateString(endDate),
            pageSize: pageSize,
            pageIndex: currentPage,
            sortBy: sortBy
        });
        fetch(`/api/aggregate/${itemType}/${getUploadId()}?${params}`, {
            method: "GET",
            headers: getHeaders()
        })
            .then(r => {
                if (r.ok) {
                    r.json().then(json => {
                        setTableData(json);
                    })
                }
            })
    }, [itemType, startDate, endDate, sortBy, searchTerm, pageSize, currentPage]);

    return (
        <div className="page" style={{marginInline: "40px"}}>
            <div style={{paddingTop: "30px", display: "flex", gap: "10px"}}>
                <SearchBox setSearchText={updateSearchTerm}/>
                <ToggleButtonGroup
                    size="small"
                    value={itemType}
                    exclusive
                    onChange={(e, value) => {
                        if (value !== null) updateItemType(value)
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
            <DateSelector ref={dateSelector}
                          startDate={startDate}
                          setStartDate={setStartDate}
                          endDate={setEndDate}
                          setEndDate={setEndDate}
                          setCurrentPage={setCurrentPage}/>
            <div>
                <SongDataTable tableData = {tableData}/>
            </div>
            <div style={{display: "flex", justifyContent: "center"}}>
                <TablePagination
                    component="div"
                    sx={{".MuiTablePagination-displayedRows": {minWidth: "150px"}}}
                    count={10000}
                    onPageChange={handleMovePage}
                    page={currentPage}
                    rowsPerPage={pageSize}
                    rowsPerPageOptions={[10, 20, 50, 100]}
                    showFirstButton={true}
                    showLastButton={true}
                    onRowsPerPageChange={handleChangePageSize}/>
            </div>
        </div>
    )
}