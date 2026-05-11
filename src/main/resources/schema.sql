CREATE TABLE IF NOT EXISTS satellite_states (
    id BIGSERIAL PRIMARY KEY,
    active BOOLEAN NOT NULL,
    status_message VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS energy_systems (
    id BIGSERIAL PRIMARY KEY,
    low_battery_threshold DOUBLE PRECISION NOT NULL,
    max_battery DOUBLE PRECISION NOT NULL,
    min_battery DOUBLE PRECISION NOT NULL,
    battery_level DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS satellite_constellations (
    id BIGSERIAL PRIMARY KEY,
    constellation_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS satellites (
    id BIGSERIAL PRIMARY KEY,
    satellite_type VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    inside_temperature DOUBLE PRECISION,
    outside_temperature DOUBLE PRECISION,
    state_id BIGINT NOT NULL UNIQUE,
    energy_system_id BIGINT NOT NULL UNIQUE,
    constellation_id BIGINT,
    CONSTRAINT fk_satellite_state FOREIGN KEY (state_id) REFERENCES satellite_states(id),
    CONSTRAINT fk_satellite_energy FOREIGN KEY (energy_system_id) REFERENCES energy_systems(id),
    CONSTRAINT fk_satellite_constellation FOREIGN KEY (constellation_id) REFERENCES satellite_constellations(id)
);

CREATE TABLE IF NOT EXISTS communication_satellites (
    satellite_id BIGINT PRIMARY KEY,
    bandwidth DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_communication_satellite FOREIGN KEY (satellite_id) REFERENCES satellites(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS imaging_satellites (
    satellite_id BIGINT PRIMARY KEY,
    resolution DOUBLE PRECISION NOT NULL,
    photos_taken INTEGER NOT NULL,
    CONSTRAINT fk_imaging_satellite FOREIGN KEY (satellite_id) REFERENCES satellites(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_constellation_name ON satellite_constellations(constellation_name);
CREATE INDEX IF NOT EXISTS idx_satellite_name ON satellites(name);
CREATE INDEX IF NOT EXISTS idx_satellite_constellation_id ON satellites(constellation_id);
