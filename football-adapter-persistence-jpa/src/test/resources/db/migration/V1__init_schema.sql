-- Initialisation du schema PostgreSQL pour Football API

CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    acronym VARCHAR(10) NOT NULL UNIQUE,
    budget NUMERIC(12,2) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'player_position') THEN
        CREATE TYPE player_position AS ENUM (
            'GK', 'SW', 'RWB', 'LWB', 'RB', 'LB', 'CB', 'DM',
            'RW', 'LW', 'LM', 'RM', 'CM', 'AM', 'CF', 'RF', 'LF', 'ST'
        );
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS players (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    acronym VARCHAR(10) NOT NULL,
    position player_position NOT NULL,
    performance_note REAL NOT NULL CHECK (performance_note BETWEEN 0 AND 10),
    market_price NUMERIC(12,2) NOT NULL CHECK (market_price >= 0),
    is_titulaire BOOLEAN NOT NULL DEFAULT false,
    team_id BIGINT REFERENCES teams(id) ON DELETE SET NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS player_transfers (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES players(id),
    source_team_id BIGINT REFERENCES teams(id),
    target_team_id BIGINT NOT NULL REFERENCES teams(id),
    transfer_price NUMERIC(12,2) NOT NULL,
    transfer_date TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_players_team_id ON players(team_id);
CREATE INDEX IF NOT EXISTS idx_players_position ON players(position);
CREATE INDEX IF NOT EXISTS idx_players_performance ON players(performance_note);

