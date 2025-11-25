console.log("✅ media/upload.js loaded");

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Please login first!");
    window.location.href = "/login.html";
    return;
  }

  const form = document.getElementById("uploadForm");
  form.addEventListener("submit", handleUpload);
});

// ====================== 📤 HANDLE UPLOAD ======================
async function handleUpload(e) {
  e.preventDefault();

  const fileInput = document.getElementById("mediaFile");
  const description = document.getElementById("description").value.trim();

  if (!fileInput.files.length) {
    showToast("Please select a file to upload!", "red");
    return;
  }

  const file = fileInput.files[0];

  // ✅ Validate file size (< 50MB) and type
  const allowedTypes = ["image/", "video/", "audio/", "application/pdf"];
  if (!allowedTypes.some(type => file.type.startsWith(type) || (file.name.endsWith(".pdf") && file.type === "application/octet-stream"))) {
    showToast("Unsupported file type!", "red");
    return;
  }
  if (file.size > 50 * 1024 * 1024) {
    showToast("File size exceeds 50MB!", "red");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);
  formData.append("description", description);

  const progressContainer = document.getElementById("progressContainer");
  const progressBar = document.getElementById("progressBar");
  const progressPercent = document.getElementById("progressPercent");

  progressContainer.classList.remove("hidden");

  try {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/media/upload", true);
    xhr.setRequestHeader("Authorization", `Bearer ${token}`);

    xhr.upload.onprogress = (event) => {
      if (event.lengthComputable) {
        const percent = Math.round((event.loaded / event.total) * 100);
        progressBar.style.width = percent + "%";
        progressPercent.textContent = percent + "%";
      }
    };

    xhr.onload = () => {
      if (xhr.status === 200 || xhr.status === 201) {
        showToast("File uploaded successfully!", "green");
        fileInput.value = "";
        document.getElementById("description").value = "";
        progressBar.style.width = "0%";
        progressContainer.classList.add("hidden");
        showPreview(JSON.parse(xhr.responseText));
      } else {
        showToast("Upload failed!", "red");
        progressBar.style.width = "0%";
        progressContainer.classList.add("hidden");
      }
    };

    xhr.onerror = () => {
      showToast("Network error during upload!", "red");
      progressBar.style.width = "0%";
      progressContainer.classList.add("hidden");
    };

    xhr.send(formData);
  } catch (error) {
    console.error("Error uploading file:", error);
    showToast("Unexpected error!", "red");
    progressBar.style.width = "0%";
    progressContainer.classList.add("hidden");
  }
}

// ====================== 🖼️ PREVIEW ======================
function showPreview(data) {
  const container = document.getElementById("previewContainer");
  const preview = document.getElementById("previewContent");
  container.classList.remove("hidden");

  if (data.fileUrl) {
    const url = data.fileUrl;
    if (url.endsWith(".mp4") || url.includes("video")) {
      preview.innerHTML = `<video controls class="w-full rounded-lg shadow mb-4"><source src="${url}" type="video/mp4"></video>`;
    } else if (url.endsWith(".pdf")) {
      preview.innerHTML = `<iframe src="${url}" class="w-full h-64 rounded-lg shadow mb-4"></iframe>`;
    } else if (url.endsWith(".mp3") || url.includes("audio")) {
      preview.innerHTML = `<audio controls class="w-full mb-4"><source src="${url}" type="audio/mpeg"></audio>`;
    } else {
      preview.innerHTML = `<img src="${url}" alt="Preview" class="w-full rounded-lg shadow mb-4" />`;
    }
  } else {
    preview.innerHTML = `<p class='text-gray-500'>No preview available</p>`;
  }
}

// ====================== 🍞 TOAST ======================
function showToast(message, color = "blue") {
  const container = document.getElementById("toastContainer");
  const toast = document.createElement("div");
  toast.className = `bg-${color}-500 text-white px-4 py-2 mb-2 rounded-lg shadow-md animate-fadeIn`;
  toast.textContent = message;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    setTimeout(() => toast.remove(), 400);
  }, 2500);
}