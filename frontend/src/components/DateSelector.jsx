import Selector from "./Selector/Selector.jsx";
import {Stack} from "@mui/material";
import {forwardRef, useEffect, useImperativeHandle, useState} from "react";
import {getHeaders, getUploadId} from "../utils/utils.js";

export const DateSelector = forwardRef((
    {startDate, setStartDate, endDate, setEndDate, setCurrentPage}, ref) => {

    const [dateTypeSelection, setDateTypeSelection] = useState("All");

    const [yearSelectionOptions, setYearSelectionOptions] = useState([]);
    const [yearSelection, setYearSelection] = useState(2020);
    const [monthSelection, setMonthSelection] = useState(null);

    const fullMonthList = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    const [monthSelectionOptions, setMonthSelectionOptions] = useState(fullMonthList);
    const [monthListData, setMonthListData] = useState(null);


    const updateDateRange = (dateType, year, month) => {
        if (dateType === "All") {
            setStartDate(new Date(2000, 0));
            setEndDate(new Date(2040, 0));
        } else if (dateType === "Year") {
            setStartDate(new Date(year, 0));
            setEndDate(new Date(year + 1, 0));
        } else if (dateType === "Month") {
            let currentMonth = fullMonthList.indexOf(month);
            let followingMonth = currentMonth + 1
            let followingYear = year
            if (currentMonth === 11) {
                followingMonth = 0
                followingYear = year + 1
            }
            setStartDate(new Date(year, currentMonth));
            setEndDate(new Date(followingYear, followingMonth));
        }


    }

    const updateDateTypeSelection = (newDateType) => {
        setCurrentPage(0);
        updateDateRange(newDateType, yearSelection, monthSelection);
        setDateTypeSelection(newDateType);
    }
    const updateYearSelection = (newYear) => {
        setCurrentPage(0);
        let newMonth = updateMonthList(monthListData, newYear);
        updateDateRange(dateTypeSelection, newYear, newMonth);
        setYearSelection(newYear);
    }
    const updateMonthSelection = (newMonth) => {
        setCurrentPage(0);
        updateDateRange(dateTypeSelection, yearSelection, newMonth);
        setMonthSelection(newMonth);
    }

    const updateMonthList = (newMonthListData, year) => {
        if (newMonthListData !== null && newMonthListData.hasOwnProperty(year)) {
            setMonthSelectionOptions(newMonthListData[year])
            if (!(newMonthListData[year].includes(monthSelection)) && newMonthListData[year].length > 0) {
                setMonthSelection(newMonthListData[year][0])
                return newMonthListData[year][0]
            }
        } else {
            setMonthSelectionOptions([])
        }
        return monthSelection;
    }

    const updateDatesAvailable = (searchTerm, itemType) => {
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
                            monthIndex => fullMonthList[monthIndex - 1]
                        );
                    })
                    setYearSelectionOptions(years);
                    setMonthListData(monthListData);
                    updateMonthList(monthListData, yearSelection);
                })
            } else {
                console.log("ERROR: upload data info failed to load, id: ", getUploadId())
            }
        })
    }
    useImperativeHandle(ref, () => ({updateDatesAvailable, }));


    return (
        <Stack style={{alignItems: "center"}}>
            <div style={{minWidth: "400px", width: "30%", padding: "12px"}}>
                <Selector
                    items={["All", "Year", "Month", "Custom"]}
                    selectedValue={dateTypeSelection}
                    setSelectedValue={updateDateTypeSelection}
                />
            </div>
            <div>
                {dateTypeSelection === "Year" || dateTypeSelection === "Month" ?
                    <Stack style={{alignItems: "center", gap: "15px"}}>
                        <Selector
                            style={{minWidth: `calc(100px * ${yearSelectionOptions.length})`}}
                            items={yearSelectionOptions}
                            selectedValue={yearSelection}
                            setSelectedValue={updateYearSelection}
                            isNumber={true}
                            noItemsString={"No Songs Found - Can't Select Year"}
                        />
                        {dateTypeSelection === "Month" ?
                            <Selector
                                style={{minWidth: `calc(60px * ${monthSelectionOptions.length})`}}
                                items={monthSelectionOptions}
                                selectedValue={monthSelection}
                                setSelectedValue={updateMonthSelection}
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
    )
})
