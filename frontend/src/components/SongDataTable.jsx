import {Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {msPlayedToString} from "../utils/utils.js";


export default function SongDataTable({tableData}) {

    const rowNames = [];
    if (tableData && tableData.length>0) {
        if (tableData[0].hasOwnProperty("trackName")) {
            rowNames.push({title: "Track", dataName: "trackName"})
        }
        if (tableData[0].hasOwnProperty("artistName")) {
            rowNames.push({title: "Artist", dataName: "artistName"})
        }
        if (tableData[0].hasOwnProperty("albumName")) {
            rowNames.push({title: "Album", dataName: "albumName"})
        }
    }

    return (
        <TableContainer>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell></TableCell>
                        {rowNames.map((cols) => (
                            <TableCell>{cols.title}</TableCell>
                        ))}
                        <TableCell>Listen Time</TableCell>
                        <TableCell>Listens</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {tableData.map((row) => (
                        <TableRow>
                            <TableCell>{row.preSearchIndex}</TableCell>
                            {rowNames.map((cols) => (
                                <TableCell>{row[cols.dataName]}</TableCell>
                            ))}
                            <TableCell>{msPlayedToString(row.totalMsPlayed)}</TableCell>
                            <TableCell>{row.listens}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    )
}