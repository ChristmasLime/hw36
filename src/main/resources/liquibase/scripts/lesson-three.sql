-- liquibase formatted sql
-- changeset your-name:1

CREATE INDEX idx_student_name ON student (name);

-- changeset your-name:2

CREATE INDEX idx_faculty_name_color ON faculty (name, color);