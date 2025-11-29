# Learning Management System (LMS)

A clean and simple Learning Management System built with **Spring Boot 3**, **Thymeleaf**, **MySQL**, and **Tailwind CSS**.

✔ Role-based system: **Admin**, **Instructor**, **Student**  
✔ Secure **JWT Authentication**  
✔ Full CRUD for Courses & Lessons  
✔ Instructor uploads lessons & PDF notes  
✔ Students enroll and learn  
✔ Admin manages entire platform

---
## 🚀 Deployment
**Both Backend and Frontend are fully deployed on Render.**
This makes the LMS accessible online for testing, demonstration, and evaluation.

---
## 🔐 Admin Registration Notice
Admin signup is **not** available in the public UI.  
To create an Admin, use the temporary testing endpoint:

```
https://lms-oy3b.onrender.com/admin/temp/signup
```

⚠ This is only for testing on Render.  
Instructors and Students can sign up normally using the main signup page.

---
## 🏗 Tech Stack
**Backend:** Spring Boot 3, Spring Security (JWT), MySQL, JPA/Hibernate  
**Frontend:** Thymeleaf, Tailwind CSS  
**Storage:** MySQL (Render Cloud DB)  
**Deployment:** Render (Backend + Frontend)

---
## 📌 Features
- Admin: Manage users, courses, approvals
- Instructor: Create courses, upload lessons, add PDFs
- Student: Enroll, view courses, access lessons
- Secure login + dashboards for each role

---
## 📁 Project Modules
- **Auth Module** – JWT login/signup for Instructor & Student
- **Admin Module** – Approvals, user & course management
- **Course Module** – Course CRUD
- **Lesson Module** – Lesson creation + PDF uploads
- **Enrollment Module** – Student enrollments

---
## © Author
Developed by **Ajith** as part of Full Stack Development learning and LMS project practice.
