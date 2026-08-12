
export function getHeaders() {
    let token = localStorage.getItem("authToken");

    return {
        Authorization: `Bearer ${token}`
    }
}

export function getHeadersJson() {
    let headers = getHeaders();
    headers["Content-Type"] = "application/json";
    return headers;
}

export function logout() {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("authToken");
    localStorage.removeItem("username");
}

export function toDateString(date) {
    let out = date.toISOString()
               .replace("T", " ")
               .replace("Z", "")
               .replace("+", " ");
    return out;
}