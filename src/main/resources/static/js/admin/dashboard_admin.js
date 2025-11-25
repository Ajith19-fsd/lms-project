console.log("✅ dashboard_admin.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "ADMIN") {
        alert("Access denied! Only Admins can view this page.");
        window.location.href = "/login";
        return;
    }

    const statsContainer = document.getElementById("statsContainer");
    const logoutBtn = document.getElementById("logoutBtn");
    const adminName = document.getElementById("adminName");
    const toastContainer = document.getElementById("toastContainer");

    // ----- Toast helper -----
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

    // ----- Load Admin Profile -----
    async function loadProfile() {
        try {
            const res = await fetch("/admin/api/profile", {
                method: "GET",
                credentials: "include",
                headers: {
                    Authorization: `Bearer ${token}`,
                    Accept: "application/json"
                }
            });

            if (!res.ok) {
                console.warn("Profile endpoint returned", res.status);
                adminName.textContent = localStorage.getItem("fullName") ?? "Admin";
                return;
            }

            const profile = await res.json();

            // Save fullName in localStorage for backup
            if (profile.fullName) {
                localStorage.setItem("fullName", profile.fullName);
            }

            adminName.textContent = profile.fullName ?? "Admin";

        } catch (err) {
            console.error("❌ Error loading profile:", err);
            adminName.textContent = localStorage.getItem("fullName") ?? "Admin";
            showToast("Failed to load profile", "error");
        }
    }

    // ----- Load Stats -----
    async function loadStats() {
        try {
            const res = await fetch("/api/admin/stats", {
                headers: {
                    Authorization: `Bearer ${token}`,
                    Accept: "application/json"
                }
            });
            if (!res.ok) throw new Error("Failed to load stats: " + res.status);

            const stats = await res.json();

            statsContainer.innerHTML = `
                <div class="bg-green-100 p-4 rounded-lg">
                    <p class="text-gray-700 font-medium">Users</p>
                    <p class="text-2xl font-bold text-green-700">${stats.totalUsers}</p>
                </div>
                <div class="bg-green-100 p-4 rounded-lg">
                    <p class="text-gray-700 font-medium">Instructors</p>
                    <p class="text-2xl font-bold text-green-700">${stats.totalInstructors}</p>
                </div>
                <div class="bg-green-100 p-4 rounded-lg">
                    <p class="text-gray-700 font-medium">Students</p>
                    <p class="text-2xl font-bold text-green-700">${stats.totalStudents}</p>
                </div>
                <div class="bg-green-100 p-4 rounded-lg">
                    <p class="text-gray-700 font-medium">Courses</p>
                    <p class="text-2xl font-bold text-green-700">${stats.totalCourses}</p>
                </div>
            `;
        } catch (err) {
            console.error("❌ Error loading stats:", err);
            showToast("Failed to load stats", "error");
        }
    }

    await loadProfile();
    await loadStats();

    // ----- Logout -----
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.clear();
            showToast("👋 Logged out successfully!", "success");
            setTimeout(() => window.location.href = "/login", 500);
        });
    }
});