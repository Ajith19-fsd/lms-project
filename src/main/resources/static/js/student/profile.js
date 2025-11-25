console.log("✅ profile.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "STUDENT") {
        alert("Access denied! Please login as Student.");
        window.location.href = "/login";
        return;
    }

    const toastContainer = document.getElementById("toastContainer");
    const profileForm = document.getElementById("profileForm");
    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

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

    // -----------------------------
    // Load profile from backend
    // -----------------------------
    async function loadProfile() {
        try {
            const res = await fetch("/api/student/profile", {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) throw new Error("Failed to fetch profile");

            const data = await res.json();
            nameInput.value = data.name || "";
            emailInput.value = data.email || "";
        } catch (err) {
            console.error(err);
            showToast("Failed to load profile.", "error");
        }
    }

    // -----------------------------
    // Submit profile update
    // -----------------------------
    profileForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const body = {
            name: nameInput.value,
            email: emailInput.value,
            password: passwordInput.value
        };

        try {
            const res = await fetch("/api/student/profile", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });

            if (!res.ok) throw new Error("Failed to update profile");

            const updatedProfile = await res.json();
            nameInput.value = updatedProfile.name || "";
            emailInput.value = updatedProfile.email || "";
            passwordInput.value = "";

            showToast("Profile updated successfully!");
        } catch (err) {
            console.error(err);
            showToast("Failed to update profile.", "error");
        }
    });

    await loadProfile();
});