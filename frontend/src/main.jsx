import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Layout from "./Layout.jsx";
import Home from "./pages/Home.jsx";
import Upload from "./pages/Upload.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Profile from "./pages/Profile.jsx";
import TablePage from "./pages/TablePage.jsx";


const router = createBrowserRouter([
    {
        element: <Layout/>,
        children: [
            {path: "/", element: <Home />},
            {path: "/upload", element: <Upload />},
            {path: "/login", element: <Login />},
            {path: "/register", element: <Register />},
            {path: "/profile", element: <Profile />},
            {path: "/table", element: <TablePage />}
        ]
    }
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router}/>
  </StrictMode>,
)
