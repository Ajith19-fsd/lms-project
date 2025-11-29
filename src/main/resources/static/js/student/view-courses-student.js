console.log("✅ view-courses-student.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
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

    try {
        // Fetch all courses
        const resCourses = await fetch("/api/courses", {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!resCourses.ok) throw new Error("Failed to fetch courses");
        const courses = await resCourses.json();

        // Fetch student's enrolled courses
        const resEnrollments = await fetch("/api/student/my-courses", {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!resEnrollments.ok) throw new Error("Failed to fetch enrolled courses");
        const enrollments = await resEnrollments.json();
        const enrolledCourseIds = enrollments.map(e => e.courseId);

        if (!courses.length) {
            coursesContainer.innerHTML = `<p class="text-gray-500 text-center">No courses available.</p>`;
            return;
        }

        coursesContainer.innerHTML = "";

        // Render course cards
        courses.forEach(course => {
            const isEnrolled = enrolledCourseIds.includes(course.id);
            const card = document.createElement("div");
            card.classList.add("course-card", "bg-white", "p-6", "rounded-xl", "shadow-md", "mb-4");

            card.innerHTML = `
                <h3 class="text-lg font-semibold">Course: ${course.title}</h3>
                <p>Instructor: ${course.instructorName || "Unknown"}</p>
                <div class="mt-3 space-x-2">
                    <button class="view-lessons-btn bg-green-600 text-white px-4 py-2 rounded"
                            data-course-id="${course.id}">
                        View Lessons
                    </button>
                    <button class="enroll-btn ${isEnrolled ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'}
                            text-white px-4 py-2 rounded"
                            data-course-id="${course.id}" ${isEnrolled ? "disabled" : ""}>
                        ${isEnrolled ? "Enrolled" : "Enroll"}
                    </button>
                </div>
            `;
            coursesContainer.appendChild(card);
        });

        // Event listeners
        document.querySelectorAll(".view-lessons-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const courseId = btn.getAttribute("data-course-id");
                window.location.href = `/student/course/lessons?courseId=${courseId}`;
            });
        });

        document.querySelectorAll(".enroll-btn").forEach(btn => {
            btn.addEventListener("click", async () => {
                const courseId = btn.getAttribute("data-course-id");
                if (btn.disabled) return;

                try {
                    const res = await fetch(`/api/enrollment/enroll`, {
                        method: "POST",
                        headers: {
                            "Authorization": `Bearer ${token}`,
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({ courseId })
                    });

                    const data = await res.json();
                    if (!res.ok) throw new Error(data.message || "Failed to enroll");

                    btn.textContent = "Enrolled";
                    btn.classList.remove("bg-blue-600", "hover:bg-blue-700");
                    btn.classList.add("bg-gray-400", "cursor-not-allowed");
                    btn.disabled = true;

                    showToast("Enrolled successfully!");
                } catch (err) {
                    console.error(err);
                    showToast(err.message || "Failed to enroll", "error");
                }
            });
        });

    } catch (err) {
        console.error(err);
        coursesContainer.innerHTML = `<p class="text-red-500 text-center">Unable to load courses.</p>`;
    }

    function showToast(msg, type = "success") {
        const toastContainer = document.getElementById("toastContainer");
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
