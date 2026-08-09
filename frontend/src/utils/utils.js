
export function getHeaders() {
    let token = localStorage.getItem("authToken");

    return {
        Authorization: `Bearer ${token}`
    }
}

export function logout() {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("authToken");
    localStorage.removeItem("username");
}