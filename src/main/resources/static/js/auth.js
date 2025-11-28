async function handleSignup(event) {
    event.preventDefault();

    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const role = document.getElementById("role").value.trim();

    if (!role) {
        showToast("Please select a role");
        return;
    }

    try {
        const response = await fetch(`/api/auth/signup/${role}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ fullName, email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            showToast(data.message || "Signup Failed");
            return;
        }

        // Save user details
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("userId", data.userId);

        redirectUser(data.role);

    } catch (err) {
        console.error(err);
        showToast("Signup error: Cannot connect to server");
    }
}

async function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            showToast(data.message || "Login Failed");
            return;
        }

        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("userId", data.userId);

        redirectUser(data.role);

    } catch (err) {
        console.error(err);
        showToast("Login error: Cannot connect to server");
    }
}

function redirectUser(role) {
    switch (role?.toUpperCase()) {
        case "ADMIN":
            window.location.href = "/admin/dashboard";
            break;
        case "INSTRUCTOR":
            window.location.href = "/instructor/dashboard";
            break;
        case "STUDENT":
            window.location.href = "/student/dashboard";
            break;
        default:
            localStorage.clear();
            window.location.href = "/login";
    }
}

async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem("token");
    options.headers = {
        ...options.headers,
        "Authorization": "Bearer " + token
    };
    return fetch(url, options);
}

function showToast(message) {
    const toast = document.getElementById("toast");
    if (toast) {
        toast.innerText = message;
        toast.classList.remove("hidden");
        setTimeout(() => toast.classList.add("hidden"), 3000);
    } else {
        alert(message);
    }
}