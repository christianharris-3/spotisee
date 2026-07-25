import Topbar from "./components/Topbar/Topbar.jsx";
import {Outlet } from "react-router-dom";
import "./styles/globals.css";

export default function Layout() {
    return (
        <>
        <Topbar />
        <main className="page-container">
            <Outlet />
        </main>
        </>
    )
}