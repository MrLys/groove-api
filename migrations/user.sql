CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY NOT NULL,
    username CITEXT NOT NULL UNIQUE,
    email CITEXT NOT NULL UNIQUE,
    digest TEXT NOT NULL,
    failed_attempts INTEGER default 0,
    refresh_token TEXT,
    activated BOOLEAN DEFAULT false NOT NULL
);
