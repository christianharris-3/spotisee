
import "./buttons.css";
import {Button} from "@mui/material";


export default function CustomBadButton(data) {
    return (
        // <Button {data}></Button>
        // <Pressable
        //     onPress = {data.onPress}
        //     className="button-primary"
        // >
        //     <Text>{data.title}</Text>
        // </Pressable>
        <span className="button-primary" onClick={data.onClick}>{data.text}</span>
    );
}