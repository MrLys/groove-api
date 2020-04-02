CREATE TABLE IF NOT EXISTS "user_habit" (
    id SERIAL PRIMARY KEY NOT NULL,
    habit_id INTEGER REFERENCES public.habit(id) ON DELETE CASCADE,
    owner_id INTEGER REFERENCES public.user(id) ON DELETE CASCADE,
    CONSTRAINT unq_habit_id_owner_id UNIQUE (habit_id, owner_id)
);
