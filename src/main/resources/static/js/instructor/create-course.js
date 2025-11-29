console.log("✅ create-course.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const toastContainer = document.getElementById("toastContainer");

    if (!token || role !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    const form = document.getElementById("createCourseForm");

    async function refreshStats() {
        try {
            const resStats = await fetch("/api/instructor/courses/count", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!resStats.ok) return;
            const stats = await resStats.json();
            const totalCoursesElem = document.getElementById("totalCourses");
            const totalLessonsElem = document.getElementById("totalLessons");

            if (totalCoursesElem) totalCoursesElem.textContent = stats.totalCourses;
            if (totalLessonsElem) totalLessonsElem.textContent = stats.totalLessons;
        } catch (err) {
            console.error("Failed to refresh dashboard stats", err);
        }
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        const body = {
            title: formData.get("title"),
            description: formData.get("description")
        };

        try {
            const res = await fetch("/api/courses", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });

            if (!res.ok) {
                const errorData = await res.json();
                throw new Error(errorData.message || "Failed to create course");
            }

            showToast("Course created successfully!");
            refreshStats(); // ✅ Update counts
            setTimeout(() => window.location.href = "/instructor/my-courses", 1000);

        } catch (err) {
            console.error(err);
            showToast(err.message || "Failed to create course.", "error");
        }
    });

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
});