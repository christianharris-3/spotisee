import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";
import {Delete} from "@mui/icons-material";


export default function ConfirmDialog({open, setOpen, onDelete, message}) {


    function deleteButtonPressed() {
        setOpen(false);
        onDelete()
    }

    return (
        <Dialog open={open}>
            <DialogTitle>
                Are you sure?
            </DialogTitle>
            <DialogContent>
                <DialogContentText>
                    {message}
                </DialogContentText>
            </DialogContent>
            <DialogActions style={{display: "flex", justifyContent: "space-between"}}>
                <Button variant="outlined" color="primary" onClick={() => {setOpen(false)}}>
                    Cancel
                </Button>
                <Button variant="contained" color="error" onClick={deleteButtonPressed} startIcon={<Delete />}>
                    Delete
                </Button>
            </DialogActions>
        </Dialog>
    )
}