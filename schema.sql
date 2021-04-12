CREATE TABLE IF NOT EXISTS URL_SHORTEN (
    short_url_code CHAR(8) NOT NULL,
    origin_url VARCHAR(2083) NOT NULL,
    req_count INT NOT NULL DEFAULT 1,
    PRIMARY KEY (short_url_code)
);

CREATE INDEX origin_url_idx
ON URL_SHORTEN (origin_url);