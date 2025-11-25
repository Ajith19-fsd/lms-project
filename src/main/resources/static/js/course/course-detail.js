console.log("✅ course_detail.js loaded");

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (!token) {
        alert("Please login first!");
        window.location.href = "/login";
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const courseId = urlParams.get("id");

    if (!courseId) {
        alert("Invalid course ID!");
        return;
    }

    const loader = document.getElementById("loader");
    const courseTitle = document.getElementById("courseTitle");
    const courseInstructor = document.getElementById("courseInstructor");
    const courseCategory = document.getElementById("courseCategory");
    const courseDescription = document.getElementById("courseDescription");
    const coursePrice = document.getElementById("coursePrice");
    const enrollBtn = document.getElementById("enrollBtn");
    const enrollSection = document.getElementById("enrollSection");
    const divider = document.getElementById("divider");

    try {
        const response = await fetch(`/api/courses/${courseId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Failed to load course details");

        const course = await response.json();
        console.log("📘 Course Data:", course);

        // Populate UI
        courseTitle.textContent = course.title || "Untitled Course";
        courseInstructor.textContent = `Instructor: ${course.instructorName || "Unknown"}`;
        courseCategory.textContent = `Category: ${course.category || "General"}`;
        courseDescription.textContent = course.description || "No description available.";
        coursePrice.textContent = `Price: ₹${course.price ?? "Free"}`;

        // Show content
        loader.style.display = "none";
        courseTitle.classList.remove("hidden");
        courseInstructor.classList.remove("hidden");
        courseCategory.classList.remove("hidden");
        courseDescription.classList.remove("hidden");
        enrollSection.classList.remove("hidden");
        divider.classList.remove("hidden");

        if (role === "STUDENT") {
            enrollBtn.addEventListener("click", async () => {
                if (!confirm("Do you want to enroll in this course?")) return;

                try {
                    const enrollRes = await fetch(`/api/student/courses/${courseId}/enroll`, {
                        method: "POST",
                        headers: { "Authorization": `Bearer ${token}` }
                    });

                    if (enrollRes.ok) {
                        alert("✅ Successfully enrolled!");
                        window.location.href = "/student/view-courses";
                    } else {
                        const errorData = await enrollRes.json();
                        alert(`❌ Enrollment failed: ${errorData.message || "Unknown error"}`);
                    }
                } catch (err) {
                    console.error(err);
                    alert("❌ Enrollment failed due to network error!");
                }
            });
        } else {
            enrollSection.style.display = "none"; // hide for non-students
        }

    } catch (error) {
        console.error("Error loading course:", error);
        loader.textContent = "Failed to load course details. Please try again later.";
    }
});