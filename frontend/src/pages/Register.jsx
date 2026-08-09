import {Box, Button, Link, Paper, Stack, TextField, Typography} from "@mui/material";
import {useState} from "react";
import {useNavigate} from "react-router-dom";

export default function Register() {
    const [username, setUsername] = useState("");
    const [password1, setPassword1] = useState("");
    const [password2, setPassword2] = useState("");
    const [invalidUsernameError, setInvalidUsernameError] = useState(false);
    const [passwordsNotEqualError, setPasswordsNotEqualError] = useState(false);
    const [usernameEmptyError, setUsernameEmptyError] = useState(false);
    const [passwordEmptyError, setPasswordEmptyError] = useState(false);

    const navigate = useNavigate();

    function registerButtonPress() {
        if (password1 !== password2) {
            setPasswordsNotEqualError(true);
            return
        }
        if (username === "") {
            setUsernameEmptyError(true);
            return
        }
        if (password1 === "") {
            setPasswordEmptyError(true);
            return
        }
        setPasswordsNotEqualError(false);
        setInvalidUsernameError(false);
        setUsernameEmptyError(false);
        setPasswordEmptyError(false);

        fetch("/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({username: username, password: password1})
        }).then(r => {
            if (r.ok) {
                navigate("/login");
            } else {
                setInvalidUsernameError(true);
            }
        })
    }

    return (
        <div className="page">
            <div style={{paddingTop: "25vh"}}>
                <Paper style={{width: "400px", margin: "auto", padding: "20px 50px", borderRadius: "12px"}}>
                    <form>
                        <Stack spacing={2}>
                            <Typography variant="h5">Register</Typography>
                            <TextField id="outlined"
                                       label="Username"
                                       value={username}
                                       error={invalidUsernameError || usernameEmptyError}
                                       helperText={usernameEmptyError? "Username can't be empty": ""}
                                       onChange={(e) => {
                                           setUsername(e.target.value)
                                       }}></TextField>
                            <TextField id="outlined-password"
                                       label="Password"
                                       type="password1"
                                       value={password1}
                                       error={passwordsNotEqualError || passwordEmptyError}
                                       helperText={passwordEmptyError? "Password can't be empty": ""}
                                       onChange={(e) => {
                                           setPassword1(e.target.value)
                                       }}></TextField>
                            <TextField id="outlined-password"
                                       label="Repeat Password"
                                       type="password2"
                                       value={password2}
                                       error={passwordsNotEqualError || passwordEmptyError}
                                       onChange={(e) => {
                                           setPassword2(e.target.value)
                                       }}></TextField>
                            {invalidUsernameError ?
                                <Typography variant="subtitle2" style={{color: "red"}}>Username Already Used</Typography>
                                : <></>
                            }
                            {passwordsNotEqualError ?
                                <Typography variant="subtitle2" style={{color: "red"}}>Passwords Don't Match</Typography> : <></>
                            }
                            <Button variant="contained" onClick={registerButtonPress}> Create Account </Button>
                            <Typography variant="body2">
                                Already have an Account? {" "}
                                <Link href="/login">Sign In</Link>
                            </Typography>
                        </Stack>
                    </form>
                </Paper>
            </div>
        </div>
    )
}