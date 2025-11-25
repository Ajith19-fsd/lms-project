console.log("✅ FINAL lesson-view-student.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const lessonId = window.lessonIdFromPage;
    const courseId = window.courseIdFromPage;

    const titleEl = document.getElementById("lessonTitle");
    const descEl = document.getElementById("lessonDescription");
    const mediaListEl = document.getElementById("mediaList");
    const backBtn = document.getElementById("backToLessonsBtn");
    const toastContainer = document.getElementById("toastContainer");

    if (!token || !lessonId) {
        alert("Invalid access");
        window.location.href = "/login";
        return;
    }

    mediaListEl.innerHTML = `<li class="text-gray-500">Loading...</li>`;

    try {
        const res = await fetch(`/api/student/lessons/${lessonId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Failed to fetch lesson");

        const lesson = await res.json();

        // Update title & description
        titleEl.textContent = lesson.title || "Lesson";
        descEl.textContent = lesson.content || "No description available.";

        // Clear existing list
        mediaListEl.innerHTML = "";

        // Display media files
        if (lesson.mediaFiles && lesson.mediaFiles.length > 0) {
            lesson.mediaFiles.forEach(media => {
                const li = document.createElement("li");
                li.className = "py-1";
                li.innerHTML = `<a href="${media.fileUrl}" target="_blank" class="text-blue-600 hover:underline">📄 ${media.fileName}</a>`;
                mediaListEl.appendChild(li);
            });
        } else if (lesson.fileUrl) {
            const li = document.createElement("li");
            li.className = "py-1";
            li.innerHTML = `<a href="${lesson.fileUrl}" target="_blank" class="text-blue-600 hover:underline">📄 View Lesson File</a>`;
            mediaListEl.appendChild(li);
        } else {
            mediaListEl.innerHTML = `<li class="text-gray-500">No file uploaded.</li>`;
        }

        // Back button
        if (courseId) {
            backBtn.onclick = () => {
                window.location.href = `/student/course-lessons/${courseId}`;
            };
        }

    } catch (err) {
        console.error(err);
        mediaListEl.innerHTML = `<li class="text-red-500">Failed to load lesson.</li>`;
        showToast("Failed to load lesson", "error");
    }

    function showToast(msg, type = "success") {
        if (!toastContainer) return;

        const toast = document.createElement("div");
        toast.className = `mb-2 px-4 py-2 rounded shadow text-white text-sm ${
            type === "error" ? "bg-red-600" : "bg-green-600"
        }`;
        toast.textContent = msg;
        toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 300);
        }, 2500);
    }
});