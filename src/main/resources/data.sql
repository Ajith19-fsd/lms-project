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
