CREATE TABLE IF NOT EXISTS "team" (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR (255) NOT NULL,
    habit_id INTEGER REFERENCES public.habit(id) ON DELETE CASCADE
);
