console.log("✅ view-course.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const toastContainer = document.getElementById("toastContainer");

    if (!token || role !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    const courseId = window.location.pathname.split("/").pop();

    try {
        const res = await fetch(`/api/courses/${courseId}`, { // corrected path
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) throw new Error("Failed to fetch course");
        const course = await res.json();

        document.getElementById("courseTitle").textContent = course.title;
        document.getElementById("courseDescription").textContent = course.description;
        document.getElementById("viewLessonsLink").href = `/instructor/view-lessons/${course.id}`;
    } catch (err) {
        console.error(err);
        showToast("Failed to load course", "error");
    }

    // Toast helper
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
});