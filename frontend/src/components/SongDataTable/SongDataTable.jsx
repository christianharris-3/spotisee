import "./SongDataTable.css";
import {Button, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TableRow} from "@mui/material";
import {msPlayedToString} from "../../utils/utils.js";


export default function SongDataTable({tableData}) {

    const rowNames = [];
    if (tableData && tableData.length>0) {
        if (tableData[0].hasOwnProperty("trackName")) {
            rowNames.push({title: "Track", dataName: "trackName", align: "right", colWidth: "40%"})
        }
        if (tableData[0].hasOwnProperty("artistName")) {
            rowNames.push({title: "Artist", dataName: "artistName", align: "left", colWidth: "20%"})
        }
        if (tableData[0].hasOwnProperty("albumName")) {
            rowNames.push({title: "Album", dataName: "albumName", align: "left", colWidth: "40%"})
        }
    }

    return (
        <TableContainer>
            <Table className="mainTable">
                <TableHead>
                    <TableRow className="titleRow">
                        <TableCell sx={{width: "60px"}}></TableCell>
                        {rowNames.map((cols) => (
                            <TableCell sx={{width: cols.colWidth}} align={cols.align}>{cols.title}</TableCell>
                        ))}
                        <TableCell sx={{width: "120px"}} align="center">Listen Time</TableCell>
                        <TableCell sx={{width: "80px"}} align="center">Listens</TableCell>
                        <TableCell sx={{width: "100px"}}></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody className="tableBody">
                    {tableData.map((row) => (
                        <TableRow key={row.preSearchIndex} className="bodyRow">
                            <TableCell size="small">{row.preSearchIndex}</TableCell>
                            {rowNames.map((cols) => (
                                <TableCell
                                    size="small"
                                    align={cols.align}
                                >
                                    {row[cols.dataName]}
                                </TableCell>
                            ))}
                            <TableCell size="small" align="center">{msPlayedToString(row.totalMsPlayed)}</TableCell>
                            <TableCell size="small" align="center">{row.listens}</TableCell>
                            <TableCell size="small" align="center"><Button size="small">Select</Button></TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    )
}