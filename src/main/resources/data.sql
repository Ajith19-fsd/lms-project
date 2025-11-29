-- =========================
-- Default Roles
-- =========================
INSERT INTO roles (name)
VALUES ('ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('INSTRUCTOR')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('STUDENT')
ON CONFLICT (name) DO NOTHING;

-- =========================
-- Default Admin User
-- =========================
-- Password is BCrypt encoded: ajith123
INSERT INTO users (full_name, username, email, password, role_id, created_at, updated_at)
VALUES (
    'Ajith',
    'Admin Ajith',
    'ajith@example.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOa6mXbRoeG1pWJcEYoq0r5r14Xim6V5W',
    (SELECT id FROM roles WHERE name='ADMIN'),
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
