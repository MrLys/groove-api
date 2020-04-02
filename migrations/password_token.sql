CREATE TABLE IF NOT EXISTS "password_token" (
    id SERIAL PRIMARY KEY NOT NULL,
    user_id INTEGER REFERENCES public.user(id) NOT NULL,
    token TEXT NOT NULL,
    expiration DATE NOT NULL,
    used BOOLEAN DEFAULT FALSE
);
