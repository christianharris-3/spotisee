import {Avatar} from "@mui/material";

export default function userAvatar({username, ...other}) {

    // code taken from https://mui.com/material-ui/react-avatar/#letter-avatars
    function stringToColor(string) {
        if (typeof string !== "string") {
            string = "default";
        }
        let hash = 0;
        let i;

        for (i = 0; i < string.length; i += 1) {
            hash = string.charCodeAt(i) + ((hash << 5) - hash);
        }

        let color = '#';

        for (i = 0; i < 3; i += 1) {
            const value = (hash >> (i * 8)) & 0x80 + 0x50;
            color += value.toString(16);
        }
        return color;
    }

    function getContent(string) {
        if (typeof string === "string") {
            let split = string.split(" ");
            if (split.length === 1) {
                return split.at(0).at(0).toUpperCase()
            }
            return (split.at(0).at(0)+split.at(1).at(0)).toUpperCase()
        }
        return ""
    }


    return (
        <Avatar {...other} style={{background: stringToColor(username)}}>
            {getContent(username)}
        </Avatar>
    )
}