CREATE TABLE visitor (
    id BIGINT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    organization VARCHAR(150)
);
CREATE TABLE pass (
    id BIGINT PRIMARY KEY,
    visitor_id BIGINT NOT NULL REFERENCES visitor(id),
    qr_code VARCHAR(150) NOT NULL UNIQUE,
    valid_from TIMESTAMP(0) NOT NULL,
    valid_to TIMESTAMP(0) NOT NULL,
    max_entries INTEGER NOT NULL CHECK (max_entries > 0),
    revoked_at TIMESTAMP(0),
    CHECK (valid_to > valid_from)
);
CREATE TABLE scan (
    id BIGINT PRIMARY KEY,
    pass_id BIGINT NOT NULL REFERENCES pass(id),
    scanned_at TIMESTAMP(0) NOT NULL,
    direction VARCHAR(5) NOT NULL CHECK (direction IN ('entry', 'exit'))
);
CREATE INDEX scan_pass_time ON scan(pass_id, direction, scanned_at);
