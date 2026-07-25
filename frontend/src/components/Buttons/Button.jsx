
import "./buttons.css";

export default function Button(text) {
    return (
        <span className="button-primary">{text.text}</span>
    );
}