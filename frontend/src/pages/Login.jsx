import {Box, Button, CircularProgress, Link, Paper, Stack, TextField, Typography} from "@mui/material";
import {useState} from "react";
import {useNavigate} from "react-router-dom";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loginError, setLoginError] = useState(false);
    const [loginErrorMessage, setLoginErrorMessage] = useState("");
    const [loginLoading, setLoginLoading] = useState(false);

    const navigate = useNavigate();

    function signInButtonPress() {
        setLoginLoading(true);
        fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({username: username, password: password})
        }).then(r => {
            setLoginLoading(false);
            if (r.status === 200) {
                r.json().then(json => {
                    if (json["token"] !== undefined) {
                        setLoginError(false);
                        localStorage.setItem("loggedIn", "true");
                        localStorage.setItem("username", username);
                        localStorage.setItem("authToken", json["token"])
                        navigate("/");
                    }
                });
            } else if (r.status === 401) {
                setLoginError(true);
                setLoginErrorMessage("Incorrect Username or Password")
            } else if (r.status === 502) {
                setLoginError(true);
                setLoginErrorMessage("Couldn't connect to server")
            }
        })
    }

    return (
        <div className="page">
            <div style={{paddingTop: "25vh"}}>
                <Paper style={{maxWidth: "400px", margin: "auto", padding: "20px 50px", borderRadius: "12px"}}>
                    <form>
                        <Stack spacing={2}>
                            <Typography variant="h5">Login</Typography>
                            <TextField id="outlined"
                                       label="Username"
                                       value={username}
                                       error={loginError}
                                       onChange={(e) => {
                                           setUsername(e.target.value)
                                       }}></TextField>
                            <TextField id="outlined-password" label="Password" type="password" value={password}
                                       error={loginError}
                                       onChange={(e) => {
                                           setPassword(e.target.value)
                                       }}></TextField>
                            {loginError ?
                                <Typography variant="subtitle2" style={{color: "red"}}>{loginErrorMessage}</Typography>
                                : <></>
                            }
                            {loginLoading ?
                                <div style={{display: "flex", justifyContent: "center"}}>
                                    <CircularProgress />
                                </div> :
                                <Button variant="contained" onClick={signInButtonPress}> Sign In </Button>
                            }
                            <Typography variant="body2">
                                No account? {" "}
                                <Link href="/register">Register</Link>
                            </Typography>
                        </Stack>
                    </form>
                </Paper>
            </div>
        </div>
    )
}