console.log("✅ view-courses.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const simpleRole = role?.startsWith("ROLE_") ? role.substring(5) : role;

    if (!token || simpleRole !== "STUDENT") {
        alert("Access denied! Please login as Student.");
        window.location.href = "/login";
        return;
    }

    const coursesContainer = document.getElementById("coursesContainer");
    const noCoursesMsg = document.getElementById("noCoursesMsg");
    const toastContainer = document.getElementById("toastContainer");

    // Show toast message
    function showToast(message, type = "success") {
        const toast = document.createElement("div");
        toast.className = `mb-2 px-4 py-2 rounded shadow text-white text-sm ${
            type === "error" ? "bg-red-600" : "bg-green-600"
        }`;
        toast.textContent = message;
        toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 500);
        }, 2500);
    }

    // Escape HTML to prevent injection
    function escapeHtml(str) {
        return String(str || "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    // Load approved courses
    async function loadCourses() {
        coursesContainer.innerHTML = `<p class="text-gray-500 col-span-full text-center">Loading courses...</p>`;
        noCoursesMsg.classList.add("hidden");

        try {
            const res = await fetch("/api/courses/approved", {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!res.ok) throw new Error("Failed to fetch courses");
            const courses = await res.json();
            coursesContainer.innerHTML = "";

            if (!courses.length) {
                noCoursesMsg.textContent = "No courses available at the moment.";
                noCoursesMsg.classList.remove("hidden");
                return;
            }

            courses.forEach(course => {
                const card = document.createElement("div");
                card.className = "bg-white shadow-md p-6 rounded-lg flex flex-col justify-between";

                card.innerHTML = `
                    <h2 class="text-xl font-bold text-green-700 mb-2">${escapeHtml(course.courseTitle)}</h2>
                    <p class="text-gray-700 mb-2">${escapeHtml(course.description || "No description provided.")}</p>
                    <p class="text-gray-600 mb-2">Instructor: ${escapeHtml(course.instructorName || "Unknown")}</p>
                    <p class="text-sm text-gray-500 mb-2">Price: ₹${Number(course.price || 0)}</p>
                    <button
                        class="mt-4 bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded transition enrollBtn"
                        data-id="${course.courseId}">
                        ${course.enrolled ? "Enrolled ✅" : "Enroll"}
                    </button>
                `;

                coursesContainer.appendChild(card);
            });

            // Enroll button click
            document.querySelectorAll(".enrollBtn").forEach(btn => {
                if (btn.textContent.includes("Enrolled")) btn.disabled = true;

                btn.addEventListener("click", async () => {
                    const courseId = btn.dataset.id;

                    try {
                        const enrollRes = await fetch(`/api/student/courses/${courseId}/enroll`, {
                            method: "POST",
                            headers: { Authorization: `Bearer ${token}` }
                        });

                        if (!enrollRes.ok) throw new Error("Enroll failed");

                        showToast("Enrolled successfully! ✔");
                        await loadCourses(); // reload to update enrolled status

                    } catch {
                        showToast("Failed to enroll. Try again later.", "error");
                    }
                });
            });

        } catch (err) {
            console.error(err);
            noCoursesMsg.textContent = "Failed to load courses. Try again later.";
            noCoursesMsg.classList.remove("hidden");
            showToast("Failed to load courses.", "error");
        }
    }

    await loadCourses();
});