CREATE TABLE users (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    username char(100),
    password char(100),
    email CHAR(100),
    token char(255),
    token_expiration TIMESTAMP,
    lat REAL,
    lon REAL
);