ALTER TABLE player_entity
    ALTER COLUMN tournament_id SET NOT NULL,
ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN faction SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_players_tournament_name'
    ) THEN
ALTER TABLE player_entity
    ADD CONSTRAINT uk_players_tournament_name
        UNIQUE (tournament_id, name, faction);
END IF;
END $$;