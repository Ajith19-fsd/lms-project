// ===================================
// 📌 dashboard.js (shared for all pages)
// ===================================

// ✅ Get headers for fetch requests with token
export function getAuthHeaders() {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/login";
        return null;
    }

    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}

// ✅ Protect dashboard pages
export function protectPage() {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (!token || !role) {
        window.location.href = "/login";
    }
}

// ✅ Logout user
export function logout() {
    localStorage.clear();
    window.location.href = "/login";
}