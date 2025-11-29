document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "ADMIN") {
        alert("Access denied! Only Admins can view this page.");
        window.location.href = "/login";
        return;
    }

    const filterTabs = document.querySelectorAll(".filter-tab");
    let currentStatus = "ALL";

    // Filter tab click
    filterTabs.forEach(tab => {
        tab.addEventListener("click", () => {
            filterTabs.forEach(t => t.classList.remove("active-tab"));
            tab.classList.add("active-tab");
            currentStatus = tab.dataset.status;
            loadCourses(currentStatus);
        });
    });

    document.getElementById("logoutBtn")?.addEventListener("click", handleLogout);

    loadCourses(currentStatus);

    // ---------------------------------------------
    // LOAD COURSES API CALL
    // ---------------------------------------------
    async function loadCourses(status) {
        try {
            let url = `/api/admin/courses`;
            if (status !== "ALL") url += `?status=${status}`;

            const res = await fetch(url, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!res.ok) throw new Error("Failed to load courses");
            const courses = await res.json();
            renderCourses(courses);
        } catch (error) {
            console.error(error);
            showToast("Failed to load courses", "red");
        }
    }

    // ---------------------------------------------
    // RENDER COURSES
    // ---------------------------------------------
    function renderCourses(courses) {
        const container = document.getElementById("coursesContainer");
        container.innerHTML = "";

        if (!courses || courses.length === 0) {
            container.innerHTML = `<p class="text-gray-600 text-center col-span-full">No courses found.</p>`;
            return;
        }

        courses.forEach(course => container.innerHTML += createCourseCard(course));
    }

    // ---------------------------------------------
    // COURSE CARD TEMPLATE
    // ---------------------------------------------
    function createCourseCard(course) {
        const statusColors = {
            PENDING: "text-yellow-700 bg-yellow-100",
            APPROVED: "text-green-700 bg-green-100",
            REJECTED: "text-red-700 bg-red-100"
        };

        return `
            <div class="bg-white p-5 rounded-2xl shadow hover:shadow-lg transition-all animate-fadeIn">
                <h3 class="text-xl font-semibold text-gray-800 mb-1">${course.title}</h3>

                <p class="text-gray-600 text-sm">
                    Instructor: ${course.instructorName || "Unknown"}
                </p>

                <p class="text-gray-600 text-sm mt-1">
                    Lessons: ${course.totalLessons}
                </p>

                <p class="mt-2 inline-block px-3 py-1 rounded-full text-sm font-medium ${statusColors[course.status]}">
                    ${course.status}
                </p>

                <div class="mt-4 flex gap-2">

                    ${course.status === "PENDING" ? `
                        <button onclick="approveCourse(${course.id}, true)"
                            class="flex-1 bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg">
                            ✅ Approve
                        </button>

                        <button onclick="approveCourse(${course.id}, false)"
                            class="flex-1 bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-lg">
                            ❌ Reject
                        </button>
                    ` : ``}

                    <button onclick="deleteCourse(${course.id})"
                        class="flex-1 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg">
                        🗑 Delete
                    </button>
                </div>
            </div>
        `;
    }

    // ---------------------------------------------
    // APPROVE / REJECT COURSE
    // ---------------------------------------------
    window.approveCourse = async function(courseId, isApproved) {
        try {
            const res = await fetch(`/api/admin/course/approve`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    courseId: courseId,
                    approved: isApproved
                })
            });

            if (!res.ok) {
                showToast("Failed to update course status", "red");
                return;
            }

            showToast(isApproved ? "Course Approved!" : "Course Rejected!", "green");
            loadCourses(currentStatus);

        } catch (error) {
            console.error(error);
            showToast("Error updating course", "red");
        }
    };

    // ---------------------------------------------
    // DELETE COURSE
    // ---------------------------------------------
    window.deleteCourse = async function(courseId) {
        if (!confirm("Are you sure you want to delete this course?")) return;

        try {
            const res = await fetch(`/api/admin/course/${courseId}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                showToast("Course deleted!", "red");
                loadCourses(currentStatus);
            } else {
                showToast("Failed to delete course", "red");
            }
        } catch {
            showToast("Error deleting course", "red");
        }
    };

    // ---------------------------------------------
    // LOGOUT
    // ---------------------------------------------
    function handleLogout() {
        if (confirm("Are you sure you want to logout?")) {
            localStorage.clear();
            showToast("Logged out successfully!", "red");
            setTimeout(() => (window.location.href = "/login"), 800);
        }
    }

    // ---------------------------------------------
    // TOAST
    // ---------------------------------------------
    function showToast(message, color = "blue") {
        const container = document.getElementById("toastContainer");
        const toast = document.createElement("div");
        toast.className = `bg-${color}-500 text-white px-4 py-2 mb-2 rounded-lg shadow-md animate-fadeIn`;
        toast.textContent = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 400);
        }, 1800);
    }
});