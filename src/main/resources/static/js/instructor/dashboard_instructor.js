console.log("✅ dashboard_instructor.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    if (!token || role !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    const instructorName = document.getElementById("instructorName");
    const totalCourses = document.getElementById("totalCourses");
    const totalLessons = document.getElementById("totalLessons");
    const toastContainer = document.getElementById("toastContainer");
    const logoutBtn = document.getElementById("logoutBtn");

    function showToast(msg, type = "success") {
        const toast = document.createElement("div");
        toast.className = `mb-2 px-4 py-2 rounded shadow text-white text-sm ${
            type === "error" ? "bg-red-600" : "bg-green-600"
        }`;
        toast.textContent = msg;
        toastContainer.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 400);
        }, 2500);
    }

    function handleAuthFailure() {
        alert("Session expired. Please login again.");
        localStorage.clear();
        window.location.href = "/login";
    }

    async function refreshStats() {
        try {
            const res = await fetch("/api/instructor/courses/count", {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) return handleAuthFailure();
            const stats = await res.json();
            totalCourses.textContent = stats.totalCourses ?? 0;
            totalLessons.textContent = stats.totalLessons ?? 0;
        } catch (err) {
            console.error(err);
            showToast("Failed to load stats", "error");
        }
    }

    async function loadProfile() {
        try {
            const res = await fetch("/api/instructor/profile", {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) return handleAuthFailure();
            const profile = await res.json();
            instructorName.textContent = profile.fullName ?? profile.username ?? "Instructor";
        } catch (err) {
            console.error(err);
            showToast("Failed to load profile", "error");
        }
    }

    await loadProfile();
    await refreshStats();

    logoutBtn?.addEventListener("click", () => {
        localStorage.clear();
        showToast("Logged out successfully!", "success");
        setTimeout(() => window.location.href = "/login", 1000);
    });
});