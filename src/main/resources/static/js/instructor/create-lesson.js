console.log("✅ create-lesson.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const toastContainer = document.getElementById("toastContainer");

    if (!token || role !== "INSTRUCTOR") {
        alert("Access denied! Please login as Instructor.");
        window.location.href = "/login";
        return;
    }

    const form = document.getElementById("createLessonForm");

    // Set Course ID automatically from ?courseId=1
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("courseId")) {
        document.getElementById("courseId").value = urlParams.get("courseId");
    }

    // Handle UploadCare
    const uploader = uploadcare.Widget('[role="uploadcare-uploader"]');

    uploader.onUploadComplete(function (info) {
        document.getElementById("fileUrl").value = info.cdnUrl;
        console.log("Uploaded file:", info.cdnUrl);
    });

    // Form Submit
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const courseId = document.getElementById("courseId").value;

        const body = {
            title: document.getElementById("title").value,
            content: document.getElementById("description").value,
            fileUrl: document.getElementById("fileUrl").value || null
        };

        try {
            const res = await fetch(`/api/lessons/course/${courseId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });

            if (!res.ok) throw new Error("Failed to create lesson");

            showToast("Lesson created successfully!");
            setTimeout(() => {
                window.location.href = `/instructor/view-lessons/${courseId}`;
            }, 1000);

        } catch (err) {
            console.error(err);
            showToast(err.message || "Error creating lesson", "error");
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