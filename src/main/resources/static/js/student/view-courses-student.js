console.log("✅ view-courses-student.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    const coursesContainer = document.getElementById("courses-container");
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "STUDENT") {
        alert("Please login as Student to view your courses.");
        window.location.href = "/login";
        return;
    }

    coursesContainer.innerHTML = `<p class="text-gray-500 text-center">Loading courses...</p>`;

    fetch("/api/student/my-courses", {
        headers: { Authorization: `Bearer ${token}` }
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch enrolled courses");
            return res.json();
        })
        .then(enrollments => {
            if (!enrollments.length) {
                coursesContainer.innerHTML = `<p class="text-gray-500 text-center">No courses enrolled yet.</p>`;
                return;
            }

            coursesContainer.innerHTML = "";
            enrollments.forEach(e => {
                const card = document.createElement("div");
                card.classList.add("course-card", "bg-white", "p-6", "rounded-xl", "shadow-md", "mb-4");
                card.innerHTML = `
                    <h3 class="text-lg font-semibold">Course: ${e.courseTitle}</h3>
                    <p>Enrolled on: ${new Date(e.enrolledAt).toLocaleDateString()}</p>
                    <button class="view-btn bg-green-600 text-white px-4 py-2 rounded mt-3 view-lessons-btn"
                            data-course-id="${e.courseId}">
                        View Lessons
                    </button>
                `;
                coursesContainer.appendChild(card);
            });

            document.querySelectorAll(".view-lessons-btn").forEach(btn => {
                btn.addEventListener("click", () => {
                    const courseId = btn.getAttribute("data-course-id");
                    window.location.href = `/student/course/lessons?courseId=${courseId}`;
                });
            });
        })
        .catch(err => {
            console.error(err);
            coursesContainer.innerHTML = `<p class="text-red-500 text-center">Unable to load enrolled courses.</p>`;
        });
});