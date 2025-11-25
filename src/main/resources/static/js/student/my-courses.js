console.log("✅ my-courses.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "STUDENT") {
        alert("Access denied! Please login as Student.");
        window.location.href = "/login";
        return;
    }

    const tbody = document.querySelector("#myCoursesTable tbody");
    const toastContainer = document.getElementById("toastContainer");

    // Toast message function
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

    // Escape HTML
    function escapeHtml(str) {
        return String(str || "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    // Fetch lessons count for course
    async function fillLessonsCount(courseId, tdElement) {
        try {
            const res = await fetch(`/api/lessons/course/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) {
                tdElement.textContent = "-";
                return;
            }
            const lessons = await res.json();
            tdElement.textContent = lessons.length ?? 0;
        } catch (err) {
            console.error("Failed to fetch lessons for course", courseId, err);
            tdElement.textContent = "-";
        }
    }

    // Load student’s courses
    try {
        const res = await fetch("/api/student/my-courses", {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Failed to fetch enrolled courses");

        const courses = await res.json();
        tbody.innerHTML = "";

        if (!courses.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center py-4 text-gray-500">
                        You have not enrolled in any course yet.
                    </td>
                </tr>
            `;
            return;
        }

        for (const course of courses) {
            const tr = document.createElement("tr");
            tr.className = "border-b hover:bg-gray-50";

            // Title
            const titleTd = document.createElement("td");
            titleTd.className = "py-2 px-4 font-medium";
            titleTd.textContent = escapeHtml(course.courseTitle || "Untitled");

            // Status
            const statusTd = document.createElement("td");
            statusTd.className = "py-2 px-4";
            statusTd.textContent = escapeHtml(course.enrollmentStatus || "Enrolled");

            // Lessons count placeholder
            const lessonsTd = document.createElement("td");
            lessonsTd.className = "py-2 px-4";
            lessonsTd.textContent = "…";

            // Actions
            const actionsTd = document.createElement("td");
            actionsTd.className = "py-2 px-4";
            const viewLink = document.createElement("a");
            viewLink.href = `/student/course/${course.courseId}/lessons`;
            viewLink.className = "text-blue-600 hover:underline";
            viewLink.textContent = "View Lessons";
            actionsTd.appendChild(viewLink);

            // Append cells
            tr.appendChild(titleTd);
            tr.appendChild(statusTd);
            tr.appendChild(lessonsTd);
            tr.appendChild(actionsTd);
            tbody.appendChild(tr);

            // Async fill lessons count
            fillLessonsCount(course.courseId, lessonsTd);
        }

    } catch (err) {
        console.error(err);
        showToast(err.message || "Failed to load courses", "error");
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center py-4 text-red-600">
                    Failed to load your courses. Try again later.
                </td>
            </tr>
        `;
    }
});