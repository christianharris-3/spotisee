import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Layout from "./Layout.jsx";
import Home from "./pages/Home.jsx";
import Upload from "./pages/Upload.jsx";
import Login from "./pages/Login.jsx";


const router = createBrowserRouter([
    {
        element: <Layout/>,
        children: [
            {path: "/", element: <Home />},
            {path: "/upload", element: <Upload />},
            {path: "/login", element: <Login />}
        ]
    }
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router}/>
  </StrictMode>,
)
