console.log("✅ view-lessons.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const toastContainer = document.getElementById("toastContainer");

    if (!token || role !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    // Extract courseId from URL
    const courseId = window.location.pathname.split("/").pop();
    const tbody = document.querySelector("#lessonsTable tbody");

    // Set Add Lesson link dynamically
    document.getElementById("addLessonBtn").href =
        `/instructor/create-lesson?courseId=${courseId}`;

    try {
        const res = await fetch(`/api/lessons/course/${courseId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Failed to fetch lessons");

        const lessons = await res.json();
        tbody.innerHTML = "";

        lessons.forEach(lesson => {
            const tr = document.createElement("tr");
            tr.className = "border-b hover:bg-gray-50";

            const fileCell = lesson.fileUrl
                ? `<a href="${lesson.fileUrl}" target="_blank" class="text-blue-600 underline">View File</a>`
                : `<span class="text-gray-500">No file</span>`;

            tr.innerHTML = `
                <td class="py-2 px-4">${lesson.title}</td>
                <td class="py-2 px-4">${fileCell}</td>
                <td class="py-2 px-4 space-x-2">
                    <a href="/instructor/view-lesson/${lesson.id}" class="text-green-600 hover:underline">View</a>
                    <button data-id="${lesson.id}" class="deleteBtn text-red-600 hover:underline">Delete</button>
                </td>
            `;

            tbody.appendChild(tr);
        });

        attachDeleteEvents();

    } catch (err) {
        console.error(err);
        showToast("Failed to load lessons", "error");
    }

    // Delete Lesson
    function attachDeleteEvents() {
        document.querySelectorAll(".deleteBtn").forEach(btn => {
            btn.addEventListener("click", async e => {
                const id = e.target.dataset.id;

                if (confirm("Delete this lesson?")) {
                    try {
                        const delRes = await fetch(`/api/lessons/${id}`, {
                            method: "DELETE",
                            headers: { Authorization: `Bearer ${token}` }
                        });

                        if (!delRes.ok) throw new Error("Failed to delete lesson");

                        showToast("Lesson deleted successfully!");
                        e.target.closest("tr").remove();

                    } catch {
                        showToast("Failed to delete lesson", "error");
                    }
                }
            });
        });
    }

    // Toast
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