
export function getHeaders() {
    let token = localStorage.getItem("authToken");

    return {
        Authorization: `Bearer ${token}`
    }
}