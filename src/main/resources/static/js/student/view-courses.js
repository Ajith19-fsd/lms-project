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

    function escapeHtml(str) {
        return String(str || "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

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
                noCoursesMsg.textContent = "No courses available.";
                noCoursesMsg.classList.remove("hidden");
                return;
            }

            courses.forEach(course => {
                const card = document.createElement("div");
                card.className = "bg-white shadow-md p-6 rounded-lg flex flex-col justify-between";

                card.innerHTML = `
                    <h2 class="text-xl font-bold text-green-700 mb-2">${escapeHtml(course.title)}</h2>
                    <p class="text-gray-700 mb-2">${escapeHtml(course.description || "No description.")}</p>
                    <p class="text-gray-600 mb-2">Instructor: ${escapeHtml(course.instructorName || "Unknown")}</p>
                    <p class="text-sm text-gray-500 mb-2">Price: ₹${Number(course.price || 0)}</p>
                    <button
                        class="mt-4 bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded transition enrollBtn"
                        data-id="${course.id}">
                        ${course.enrolled ? "Enrolled ✅" : "Enroll"}
                    </button>
                `;
                coursesContainer.appendChild(card);
            });

            document.querySelectorAll(".enrollBtn").forEach(btn => {
                if (btn.textContent.includes("Enrolled")) btn.disabled = true;

                btn.addEventListener("click", async () => {
                    const courseId = btn.dataset.id;

                    try {
                        const enrollRes = await fetch(`/api/enrollment/enroll`, {
                            method: "POST",
                            headers: {
                                Authorization: `Bearer ${token}`,
                                "Content-Type": "application/json"
                            },
                            body: JSON.stringify({ courseId: Number(courseId) })
                        });

                        if (!enrollRes.ok) throw new Error("Enroll failed");

                        showToast("Enrolled successfully! ✔");
                        await loadCourses(); // reload to update enrolled status

                    } catch (err) {
                        console.error(err);
                        showToast("Failed to enroll. Try again later.", "error");
                    }
                });
            });

        } catch (err) {
            console.error(err);
            noCoursesMsg.textContent = "Failed to load courses.";
            noCoursesMsg.classList.remove("hidden");
            showToast("Failed to load courses.", "error");
        }
    }

    await loadCourses();
});