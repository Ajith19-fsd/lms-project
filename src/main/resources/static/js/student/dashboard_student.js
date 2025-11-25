console.log("✅ dashboard_student.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "STUDENT") {
        alert("Access denied! Only students can view this page.");
        window.location.href = "/login";
        return;
    }

    const studentNameElem = document.getElementById("studentName");
    const totalCoursesElem = document.getElementById("totalCourses");
    const enrolledCoursesElem = document.getElementById("enrolledCourses");
    const completedLessonsElem = document.getElementById("completedLessons");
    const pendingLessonsElem = document.getElementById("pendingLessons");
    const logoutBtn = document.getElementById("logoutBtn");
    const toastContainer = document.getElementById("toastContainer");

    function showToast(msg, type = "success") {
        const toast = document.createElement("div");
        toast.className = `mb-2 px-4 py-2 rounded shadow text-white text-sm ${type === "error" ? "bg-red-600" : "bg-green-600"}`;
        toast.textContent = msg;
        toastContainer.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 400);
        }, 2500);
    }

    async function loadProfile() {
        try {
            const res = await fetch("/api/student/profile", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Failed to fetch profile");
            const profile = await res.json();
            studentNameElem.textContent = profile.name ?? "Student";
        } catch (err) {
            console.error(err);
            showToast("Failed to load profile", "error");
        }
    }

    async function loadStats() {
        try {
            const res = await fetch("/api/student/dashboard", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Failed to load dashboard stats");

            const data = await res.json();
            totalCoursesElem.textContent = data.totalCourses ?? 0;
            enrolledCoursesElem.textContent = data.enrolledCourses ?? 0;
            completedLessonsElem.textContent = data.completedLessons ?? 0;
            pendingLessonsElem.textContent = data.pendingLessons ?? 0;
        } catch (err) {
            console.error(err);
            showToast("Failed to load stats", "error");
        }
    }

    logoutBtn?.addEventListener("click", () => {
        localStorage.clear();
        showToast("👋 Logged out successfully!");
        setTimeout(() => window.location.href = "/login", 500);
    });

    await loadProfile();
    await loadStats();
});