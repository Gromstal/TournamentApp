ALTER TABLE IF EXISTS public.pairing
    SET SCHEMA tournament_app;

ALTER TABLE IF EXISTS tournament_app.pairing
    RENAME TO pairing_entity;