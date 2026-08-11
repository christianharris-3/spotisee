import {Paper, Stack, TablePagination, ToggleButton, ToggleButtonGroup} from "@mui/material";
import {useEffect, useState} from "react";
import {getHeaders, toDateString} from "../utils/utils.js";
import SearchBox from "../components/SearchBox/SearchBox.jsx";
import Selector from "../components/Selector/Selector.jsx";

export default function TablePage() {

    const [itemType, setItemType] = useState("songs");
    const [sortBy, setSortBy] = useState("totalMsPlayed");
    const [searchTerm, setSearchTerm] = useState("");

    const [dateTypeSelection, setDateTypeSelection] = useState("All");

    const [yearSelectionOptions, setYearSelectionOptions] = useState([]);
    const [yearSelection, setYearSelection] = useState(null);
    const [monthSelection, setMonthSelection] = useState(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(100);

    const [tableData, setTableData] = useState([]);


    const handleMovePage = (event, newPage) => {
        setCurrentPage(newPage);
    }
    const handleChangePageSize = (event) => {
        setCurrentPage(0);
        setPageSize(parseInt(event.target.value, 10))
    }

    const getUploadId = () => {
        return localStorage.getItem('activeUploadId')
    }

    const getDateRange = () => {
        if (dateTypeSelection === "All") {
            return {
                startDate: new Date(2000, 0),
                endDate: new Date(2040, 0)
            }
        } else if (dateTypeSelection === "Year") {
            console.log(yearSelection)
            return {
                startDate: new Date(yearSelection, 0),
                endDate: new Date(yearSelection+1, 0)
            }
        } else if (dateTypeSelection === "Month") {
            return {
                startDate: new Date(yearSelection, monthSelection),
                endDate: new Date(yearSelection, monthSelection+1)
            }
        }



        return {
            startDate: new Date(2000, 1),
            endDate: new Date(2040, 1)
        }
    }

    useEffect(() => {
        let dates = getDateRange();
        const params = new URLSearchParams({
            searchTerm: searchTerm,
            start: toDateString(dates.startDate),
            end: toDateString(dates.endDate),
            pageSize: pageSize,
            pageIndex: currentPage,
            sortBy: sortBy
        });
        console.log("calling", params.toString(), dates)
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
                        let startDate = new Date(json.startDate)
                        let endDate = new Date(json.endDate)
                        let years = [];
                        for (let i=startDate.getFullYear(); i<=endDate.getFullYear(); i++) {
                            years.push(i);
                        }
                        setYearSelectionOptions(years)
                        if (yearSelection === null) {
                            setYearSelection(years[0]);
                        }
                    }
                )
            } else {
                console.log("ERROR: upload data info failed to load, id: ", getUploadId())
            }
        })


    }, [itemType, yearSelection, monthSelection, dateTypeSelection, sortBy, searchTerm, pageSize, currentPage]);


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
                        <Stack style={{alignItems: "center", gap: "15px"}}>
                            <Selector
                                style={{width: "400px"}}
                                items={yearSelectionOptions}
                                selectedValue={yearSelection}
                                setSelectedValue={setYearSelection}
                                isNumber={true}
                            />
                            {dateTypeSelection === "Month" ?
                                <Selector
                                    style={{minWidth: "800px"}}
                                    items={["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]}
                                    selectedValue={monthSelection}
                                    setSelectedValue={setMonthSelection}
                                /> :<div></div>
                            }
                        </Stack> :
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