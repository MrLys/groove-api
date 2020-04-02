CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY NOT NULL,
    username CITEXT NOT NULL UNIQUE,
    email CITEXT NOT NULL UNIQUE,
    digest TEXT NOT NULL,
    failed_attempts INTEGER default 0,
    refresh_token TEXT,
    activated BOOLEAN DEFAULT false NOT NULL
);

CREATE TABLE IF NOT EXISTS "habit" (
    id SERIAL PRIMARY KEY,
    name VARCHAR (255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_habit" (
    id SERIAL PRIMARY KEY NOT NULL,
    habit_id INTEGER REFERENCES public.habit(id) ON DELETE CASCADE,
    owner_id INTEGER REFERENCES public.user(id) ON DELETE CASCADE,
    CONSTRAINT unq_habit_id_owner_id UNIQUE (habit_id, owner_id)

);

CREATE TABLE IF NOT EXISTS "team" (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR (255) NOT NULL,
    habit_id INTEGER REFERENCES public.habit(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "user_team" (
    id SERIAL PRIMARY KEY NOT NULL,
    team_id INTEGER REFERENCES public.team(id) ON DELETE CASCADE,
    owner_id INTEGER REFERENCES public.user(id) ON DELETE CASCADE,
    CONSTRAINT unq_team_id_owner_id UNIQUE (team_id, owner_id)

);

CREATE TABLE IF NOT EXISTS "groove" (
    id BIGSERIAL PRIMARY KEY,
    state VARCHAR(10),
    user_habit_id INTEGER REFERENCES public.user_habit(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    owner_id INTEGER REFERENCES public.user(id) ON DELETE CASCADE,
    CONSTRAINT unq_habit_date_owner UNIQUE (user_habit_id, date, owner_id)
);

CREATE TABLE IF NOT EXISTS "activation_token" (
    id SERIAL PRIMARY KEY NOT NULL,
    user_id INTEGER REFERENCES public.user(id) NOT NULL,
    token TEXT NOT NULL,
    expiration DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS "password_token" (
    id SERIAL PRIMARY KEY NOT NULL,
    user_id INTEGER REFERENCES public.user(id) NOT NULL,
    token TEXT NOT NULL,
    expiration DATE NOT NULL,
    used BOOLEAN DEFAULT FALSE
);
