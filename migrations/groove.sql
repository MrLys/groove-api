CREATE TABLE IF NOT EXISTS "groove" (
    id BIGSERIAL PRIMARY KEY,
    state VARCHAR(10),
    user_habit_id INTEGER REFERENCES public.user_habit(id) ON DELETE CASCADE,
    date TIMESTAMP NOT NULL,
    owner_id INTEGER REFERENCES public.user(id) ON DELETE CASCADE,
    CONSTRAINT unq_habit_date_owner UNIQUE (user_habit_id, date, owner_id)
);
