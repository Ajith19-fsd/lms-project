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
-- Password is BCrypt encoded: admin123
INSERT INTO users (full_name, username, email, password, role_id, created_at, updated_at)
VALUES (
    'Admin',
    'Admin User',
    'admin@example.com',
    '$2a$10$yXGslF6x9kY1W7ZB/lkRAuE3GmW1x9u3YvYcSxF3N0Q3F5kZ9K8fS', -- bcrypt for admin123
    (SELECT id FROM roles WHERE name='ADMIN'),
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
