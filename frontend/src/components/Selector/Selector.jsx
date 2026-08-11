import "./Selector.css";
import {Divider, Link, Typography} from "@mui/material";

export default function Selector({style, items, selectedValue, setSelectedValue}) {

    const handleOnClick = (event) => {
        setSelectedValue(event.target.innerHTML);
    }

    const getIndex = () => {
        let index = items.indexOf(selectedValue);
        if (index === -1) return 0;
        return index
    }
    const getSize = () => {
        if (items) {
            return items.length
        }
        return 1
    }

    if (getSize() === 0) {
        return <div>no items found</div>
    }


    return (
        <div className="selectorBar" style={style}>
            <div className="selectorHighlight" style={{
                left: `calc(100% / ${getSize()} * ${getIndex()})`,
                width: `calc(100% / ${getSize()})`
            }}>

            </div>
            {items.map((item) =>
                    <Typography
                        underline="none"
                        onClick={handleOnClick}
                        className="selectorButton"
                        color={item === selectedValue? "primary" : "default"}>
                        {item}
                    </Typography>
            )}
        </div>
    )
}