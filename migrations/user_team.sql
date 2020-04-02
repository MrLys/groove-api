CREATE TABLE IF NOT EXISTS "user_team" (
    id SERIAL PRIMARY KEY NOT NULL,
    team_id INTEGER REFERENCES public.team(id) ON DELETE CASCADE,
    owner_id INTEGER REFERENCES public.user(id) ON DELETE CASCADE,
    CONSTRAINT unq_team_id_owner_id UNIQUE (team_id, owner_id)

);
