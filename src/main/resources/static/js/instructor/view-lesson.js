console.log("✅ view-lesson.js loaded");

document.addEventListener("DOMContentLoaded", async () => {

    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const toastContainer = document.getElementById("toastContainer");

    if (!token || role !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    const lessonId = window.location.pathname.split("/").pop();

    const titleEl = document.getElementById("lessonTitle");
    const descEl = document.getElementById("lessonDescription");
    const mediaContainer = document.getElementById("mediaContainer");
    const backBtn = document.getElementById("backToLessonsBtn");

    try {
        const res = await fetch(`/api/lessons/${lessonId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Failed to fetch lesson");

        const lesson = await res.json();

        titleEl.textContent = lesson.title;
        descEl.textContent = lesson.content || "No description available.";

        if (lesson.courseId) {
            backBtn.href = `/instructor/view-lessons/${lesson.courseId}`;
        } else {
            backBtn.style.display = "none";
        }

        // Display file
        if (lesson.fileUrl) {
            mediaContainer.innerHTML = `
                <a href="${lesson.fileUrl}" target="_blank" class="text-blue-600 underline">
                    View File
                </a>
            `;
        } else {
            mediaContainer.innerHTML = `<span class="text-gray-500">No file uploaded.</span>`;
        }

    } catch (err) {
        console.error(err);
        showToast("Failed to load lesson", "error");
    }

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