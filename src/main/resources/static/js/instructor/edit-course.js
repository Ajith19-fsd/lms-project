console.log("✅ edit-course.js loaded");

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
    const form = document.getElementById("editCourseForm");

    try {
        const res = await fetch(`/api/courses/${courseId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) throw new Error("Failed to fetch course");
        const course = await res.json();

        form.title.value = course.title;
        form.description.value = course.description;

    } catch (err) {
        console.error(err);
        showToast("Failed to load course", "error");
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const body = {
            title: form.title.value,
            description: form.description.value
        };

        try {
            const res = await fetch(`/api/courses/${courseId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });
            if (!res.ok) throw new Error("Failed to update course");
            showToast("Course updated successfully!");
            setTimeout(() => {
                window.location.href = "/instructor/my-courses";
            }, 1000);
        } catch (err) {
            console.error(err);
            showToast(err.message || "Failed to update course", "error");
        }
    });

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