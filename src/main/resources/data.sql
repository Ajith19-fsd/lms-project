-- Insert default roles only if they do not already exist

INSERT INTO roles (name)
VALUES ('ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO roles (name)
VALUES ('INSTRUCTOR')
ON CONFLICT DO NOTHING;

INSERT INTO roles (name)
VALUES ('STUDENT')
ON CONFLICT DO NOTHING;
