document.addEventListener("DOMContentLoaded", () => {

    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    // ❗ Access Protection
    if (!token || simpleRole !== "ADMIN") {
        alert("Access denied! Only Admins can view this page.");
        window.location.href = "/login";
        return;
    }

    // Tabs
    const tabs = document.querySelectorAll(".user-filter-tab");
    let allUsers = [];
    let activeRole = "ALL";

    loadUsers();

    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => t.classList.remove("active-tab"));
            tab.classList.add("active-tab");
            activeRole = tab.dataset.role;
            renderUsers();
        });
    });

    // ⭐ Load All Users
    async function loadUsers() {
        try {
            const res = await fetch("/api/admin/users", {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!res.ok) {
                showToast("Failed to load users", "red");
                return;
            }

            allUsers = await res.json();
            renderUsers();

        } catch (err) {
            showToast("Unable to fetch users", "red");
        }
    }

    // ⭐ Render user cards
    function renderUsers() {
        const container = document.getElementById("usersContainer");
        container.innerHTML = "";

        const filtered =
            activeRole === "ALL"
                ? allUsers
                : allUsers.filter(u => u.role === activeRole);

        if (!filtered.length) {
            container.innerHTML =
                `<p class="text-gray-600 text-center">No ${activeRole.toLowerCase()} users found.</p>`;
            return;
        }

        filtered.forEach(user => {
            container.innerHTML += createUserCard(user);
        });
    }

    // ⭐ User Card Component
    function createUserCard(user) {

        const roleColors = {
            ADMIN: "bg-purple-100 text-purple-700",
            INSTRUCTOR: "bg-green-100 text-green-700",
            STUDENT: "bg-blue-100 text-blue-700"
        };

        return `
        <div class="bg-white p-5 rounded-2xl shadow hover:shadow-lg transition-all animate-fadeIn">
            <h3 class="text-xl font-semibold text-gray-800 mb-1">${user.username}</h3>

            <p class="text-gray-600 text-sm">Email: ${user.email}</p>

            <span class="inline-block mt-3 px-3 py-1 rounded-full text-sm font-medium
                ${roleColors[user.role]}">
                ${user.role}
            </span>

            <div class="mt-4 flex gap-2">
                <button onclick="changeRole(${user.id})"
                    class="flex-1 bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-2 rounded-lg">
                    🔄 Change Role
                </button>

                <button onclick="deleteUser(${user.id})"
                    class="flex-1 bg-red-500 hover:bg-red-600 text-white px-3 py-2 rounded-lg">
                    🗑 Delete
                </button>
            </div>
        </div>
        `;
    }

    // ⭐ Change Role
    window.changeRole = async (userId) => {
        const newRole = prompt("Enter new role (ADMIN / INSTRUCTOR / STUDENT):");
        if (!newRole) return;

        const formattedRole = newRole.toUpperCase();

        if (!["ADMIN", "INSTRUCTOR", "STUDENT"].includes(formattedRole)) {
            showToast("Invalid role!", "red");
            return;
        }

        try {
            const res = await fetch("/api/admin/user/change-role", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({ userId, role: formattedRole })
            });

            if (res.ok) {
                showToast("User role updated!", "green");
                loadUsers();
            } else {
                showToast("Failed to update role", "red");
            }

        } catch (err) {
            showToast("Error changing role", "red");
        }
    };

    // ⭐ Delete User
    window.deleteUser = async (userId) => {
        if (!confirm("Are you sure you want to delete this user?")) return;

        try {
            const res = await fetch(`/api/admin/user/${userId}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                showToast("User deleted!", "red");
                loadUsers();
            } else {
                showToast("Failed to delete user", "red");
            }

        } catch (err) {
            showToast("Error deleting user", "red");
        }
    };

    // ⭐ Toast UI
    function showToast(message, color = "blue") {
        const container = document.getElementById("toastContainer");

        const toast = document.createElement("div");
        toast.className =
            `bg-${color}-500 text-white px-4 py-2 mb-2 rounded-lg shadow-md animate-fadeIn`;

        toast.textContent = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 400);
        }, 2000);
    }

    // ⭐ Logout
    document.getElementById("logoutBtn")?.addEventListener("click", () => {
        localStorage.clear();
        window.location.href = "/login";
    });

});