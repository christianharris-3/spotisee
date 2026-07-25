import "./topbar.css";
import "../Buttons/Button.jsx";
import Button from "../Buttons/Button.jsx";
import LogoBw from "../svg/LogoBw.jsx";

export default function Topbar() {
    const loggedIn = false;

    return (
        <header className="top-bar">
            <div className="top-divider">
                <div style={{width: "20%", display:"flex", gap: 12}}>
                    <LogoBw width={50} height={20} />
                    <span>Spotisee</span>
                </div>
                <div>
                    {loggedIn ?
                        <div>

                        </div>
                        :
                        <div style={{flex: 1, padding: 0}}>
                            <Button text="Sign In"/>
                        </div>
                    }
                </div>
            </div>
        </header>
    );
}