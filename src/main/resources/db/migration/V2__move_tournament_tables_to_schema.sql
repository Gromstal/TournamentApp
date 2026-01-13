CREATE SCHEMA IF NOT EXISTS tournament_app;

ALTER TABLE IF EXISTS public.tournament        SET SCHEMA tournament_app;
ALTER TABLE IF EXISTS public.player_entity     SET SCHEMA tournament_app;
ALTER TABLE IF EXISTS public.pairing_entity    SET SCHEMA tournament_app;
ALTER TABLE IF EXISTS public.player_opponents  SET SCHEMA tournament_app;