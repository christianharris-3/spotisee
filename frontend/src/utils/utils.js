
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

export function getUploadId() {
    return localStorage.getItem('activeUploadId')
}

export function msPlayedToString(msPlayed) {
    const totalSeconds = msPlayed/1000
    const hours = Math.floor(totalSeconds / 3600)
    const minutes = Math.floor((totalSeconds % 3600) / 60).toString()
    const seconds = Math.floor(totalSeconds % 60).toString()
    if (hours === 0) {
        return `${minutes}m ${seconds}s`
    }
    return `${hours}h ${minutes}m ${seconds}s`

}