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
    const [yearSelection, setYearSelection] = useState(2020);
    const [monthSelection, setMonthSelection] = useState(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(100);

    const [tableData, setTableData] = useState([]);

    const fullMonthList = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    const [monthSelectionOptions, setMonthSelectionOptions] = useState(fullMonthList);
    const [monthListData, setMonthListData] = useState(null);


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
            return {
                startDate: new Date(yearSelection, 0),
                endDate: new Date(yearSelection + 1, 0)
            }
        } else if (dateTypeSelection === "Month") {
            let currentMonth = fullMonthList.indexOf(monthSelection);
            let followingMonth = currentMonth + 1
            let followingYear = yearSelection
            if (currentMonth === 11) {
                followingMonth = 0
                followingYear = yearSelection + 1
            }
            return {
                startDate: new Date(yearSelection, currentMonth),
                endDate: new Date(followingYear, followingMonth)
            }
        }


        return {
            startDate: new Date(2000, 1),
            endDate: new Date(2040, 1)
        }
    }

    // Year+month date availability info
    useEffect(() => {
        const params = new URLSearchParams({
            searchTerm: searchTerm,
            itemType: itemType,
        })
        fetch(`/api/aggregation-info/${getUploadId()}?${params}`, {
            method: "GET",
            headers: getHeaders()
        }).then(r => {
            if (r.ok) {
                r.json().then(json => {
                    let years = [];
                    let monthListData = {}
                    json.forEach((item) => {
                        years.push(item.year);
                        monthListData[item.year] = item.months.map(
                            monthIndex => fullMonthList[monthIndex-1]
                        );
                    })
                    setYearSelectionOptions(years);
                    setMonthListData(monthListData);
                })
            } else {
                console.log("ERROR: upload data info failed to load, id: ", getUploadId())
            }
        })
    }, [itemType, searchTerm]);

    // update month list when changing tabs
    useEffect(() => {
        if (monthListData !== null) {
            setMonthSelectionOptions(monthListData[yearSelection])
            if (!(monthSelection in monthListData[yearSelection]) && monthListData[yearSelection].length > 0) {
                setMonthSelection(monthListData[0])
            }
        }
    }, [monthListData, yearSelection, dateTypeSelection]);

    useEffect(() => {
        setCurrentPage(0);
    }, [itemType, searchTerm, dateTypeSelection, yearSelection, monthSelection]);

    // useEffect(() => {
        // fetch(`/api/songdates/${getUploadId()}`, {
        //     method: "GET",
        //     headers: getHeaders()
        // }).then(r => {
        //     if (r.ok) {
        //         r.json().then(json => {
        //             console.log(json)
        //         })
        //     }
        // })
    // }, [itemType, searchTerm, dateTypeSelection]);

    // Load Table Data
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
                                style={{minWidth: `calc(100px * ${yearSelectionOptions.length})`}}
                                items={yearSelectionOptions}
                                selectedValue={yearSelection}
                                setSelectedValue={setYearSelection}
                                isNumber={true}
                            />
                            {dateTypeSelection === "Month" ?
                                <Selector
                                    style={{minWidth: `calc(60px * ${monthSelectionOptions.length})`}}
                                    items={monthSelectionOptions}
                                    selectedValue={monthSelection}
                                    setSelectedValue={setMonthSelection}
                                /> : <div></div>
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