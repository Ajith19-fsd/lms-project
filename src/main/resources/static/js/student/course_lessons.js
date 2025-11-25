console.log("✅ course_lessons.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;
    const toastContainer = document.getElementById("toastContainer");

    if (!token || simpleRole !== "STUDENT") {
        alert("Access denied! Please login as Student.");
        window.location.href = "/login";
        return;
    }

    const tbody = document.querySelector("#lessonsTable tbody");
    const courseIdInput = document.getElementById("courseId");
    const courseTitleInput = document.getElementById("courseTitle");

    const courseId = courseIdInput?.value;
    const courseTitle = courseTitleInput?.value;

    // Set page title dynamically
    if (courseTitle) {
        const titleEl = document.getElementById("course-title");
        if (titleEl) titleEl.textContent = courseTitle;
    }

    if (!courseId) {
        tbody.innerHTML = `<tr><td colspan="2" class="p-6 text-center text-gray-600">No course selected.</td></tr>`;
        return;
    }

    try {
        const res = await fetch(`/api/student/courses/${courseId}/lessons`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Failed to fetch lessons");

        const lessons = await res.json();
        tbody.innerHTML = "";

        if (!lessons.length) {
            tbody.innerHTML = `<tr><td colspan="2" class="p-6 text-center text-gray-600">No lessons found.</td></tr>`;
            return;
        }

        lessons.forEach(lesson => {
            const tr = document.createElement("tr");
            tr.className = "border-b hover:bg-gray-50";

            // Only Title and Actions
            tr.innerHTML = `
                <td class="py-2 px-4">${lesson.title}</td>
                <td class="py-2 px-4 space-x-2">
                    <a href="/student/course/${courseId}/lesson/${lesson.id}" class="text-green-600 hover:underline">
                        View Lesson
                    </a>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        console.error(err);
        showToast("Failed to load lessons", "error");
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
});