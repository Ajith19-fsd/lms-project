console.log("✅ my-courses.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;
    const toastContainer = document.getElementById("toastContainer");

    if (!token || simpleRole !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    const tbody = document.querySelector("#coursesTable tbody");

    // Logout
    const logoutLink = document.getElementById("logoutLink");
    if (logoutLink) {
        logoutLink.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.clear();
            window.location.href = "/login";
        });
    }

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

    // Load courses for this instructor
    try {
        const res = await fetch("/api/instructor/courses", {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (res.status === 401 || res.status === 403) {
            alert("Session expired or unauthorized. Please login again.");
            localStorage.clear();
            window.location.href = "/login";
            return;
        }

        const courses = await res.json();
        tbody.innerHTML = "";
        if (!courses || courses.length === 0) {
            tbody.innerHTML = `<tr><td class="p-6 text-center text-gray-600" colspan="4">No courses found.</td></tr>`;
            return;
        }

        courses.forEach(course => {
            const tr = document.createElement("tr");
            tr.className = "hover:bg-gray-50";
            tr.innerHTML = `
                <td class="py-3 px-4">${escapeHtml(course.title)}</td>
                <td class="py-3 px-4">${escapeHtml(course.status)}</td>
                <td class="py-3 px-4">${course.totalLessons ?? 0}</td>
                <td class="py-3 px-4 space-x-3">
                    <a href="/instructor/view-lessons/${course.id}" class="text-green-600 hover:underline">View Lessons</a>
                    <a href="/instructor/edit-course/${course.id}" class="text-yellow-600 hover:underline">Edit</a>
                    <button data-id="${course.id}" class="deleteBtn text-red-600 hover:underline">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Attach delete handlers
        document.querySelectorAll(".deleteBtn").forEach(btn => {
            btn.addEventListener("click", async (e) => {
                const id = e.target.dataset.id;
                if (!confirm("Are you sure you want to delete this course?")) return;

                try {
                    // Keep this endpoint if your CourseController exposes DELETE /api/courses/{id}
                    const delRes = await fetch(`/api/courses/${id}`, {
                        method: "DELETE",
                        headers: { Authorization: `Bearer ${token}` }
                    });

                    if (!delRes.ok) throw new Error("Failed to delete");
                    showToast("Course deleted successfully!");
                    e.target.closest("tr").remove();
                    await refreshStats(); // Refresh dashboard counts
                } catch (err) {
                    console.error(err);
                    showToast("Failed to delete course", "error");
                }
            });
        });

    } catch (err) {
        console.error(err);
        showToast("Failed to load courses", "error");
    }

    function showToast(msg, type = "success") {
        const toast = document.createElement("div");
        toast.className = `mb-2 px-4 py-2 rounded shadow text-white text-sm ${type === "error" ? "bg-red-600" : "bg-green-600"}`;
        toast.textContent = msg;
        toastContainer.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 300);
        }, 2500);
    }

    function escapeHtml(text) {
        if (!text) return "";
        return text.replace(/[&<>"'`=\/]/g, s => ( {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;',
            '/': '&#x2F;',
            '`': '&#96;',
            '=': '&#61;'
        }[s]));
    }
});