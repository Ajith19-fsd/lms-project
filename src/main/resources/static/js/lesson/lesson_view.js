console.log("✅ lesson_view.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const courseId = new URLSearchParams(window.location.search).get("courseId");
    const lessonList = document.getElementById("lessonList");
    const courseTitle = document.getElementById("courseTitle");
    const courseDescription = document.getElementById("courseDescription");

    // 🔒 Check login
    if (!token) {
        alert("Please login to view lessons.");
        window.location.href = "/login";
        return;
    }

    if (!courseId) {
        lessonList.innerHTML = `<p class="no-data text-gray-600">No course selected.</p>`;
        return;
    }

    // ---------------- Load Course Details ----------------
    async function loadCourseDetails() {
        try {
            const res = await fetch(`/api/courses/${courseId}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Failed to load course details");

            const course = await res.json();
            courseTitle.textContent = course.title || "Course Details";
            courseDescription.textContent = course.description || "No description available.";
        } catch (err) {
            console.error(err);
            courseTitle.textContent = "Error loading course";
            courseDescription.textContent = "";
        }
    }

    // ---------------- Load Lessons ----------------
    async function loadLessons() {
        try {
            const res = await fetch(`/api/lessons/course/${courseId}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Failed to load lessons");

            const lessons = await res.json();
            lessonList.innerHTML = "";

            if (!lessons || lessons.length === 0) {
                lessonList.innerHTML = `<p class="no-data text-gray-600">No lessons available for this course.</p>`;
                return;
            }

            lessons.forEach((lesson, index) => {
                const card = document.createElement("div");
                card.className = "lesson-card bg-gray-50 p-4 rounded-lg shadow mb-4 hover:shadow-md transition";
                card.innerHTML = `
                    <h4 class="font-semibold text-gray-800">${index + 1}. ${lesson.title}</h4>
                    <p class="text-gray-600 mt-1">${lesson.description || "No description provided."}</p>
                    <button class="view-btn mt-2 bg-green-600 text-white px-4 py-1 rounded hover:bg-green-700 transition"
                        onclick="viewLessonDetail(${lesson.id})">
                        View Lesson
                    </button>
                `;
                lessonList.appendChild(card);
            });

        } catch (err) {
            console.error("Error loading lessons:", err);
            lessonList.innerHTML = `<p class="error text-red-500">⚠️ Failed to load lessons. Please try again later.</p>`;
        }
    }

    // ---------------- View Lesson Detail ----------------
    window.viewLessonDetail = (lessonId) => {
        window.location.href = `/student/lesson/view?id=${lessonId}`;
    };

    // ---------------- Initialize ----------------
    await loadCourseDetails();
    await loadLessons();
});