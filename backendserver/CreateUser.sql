CREATE TABLE users (
    id INTEGER,
    username char(100),
    password char(100),
    email CHAR(100),
    token char(1000),
    token_expiration TIMESTAMP,
    lat REAL,
    lon REAL
);

ALTER TABLE users
  ADD (
    CONSTRAINT users_pk PRIMARY KEY (id)
  );
  
CREATE SEQUENCE users_sequence;

CREATE OR REPLACE TRIGGER users_on_insert
  BEFORE INSERT ON users
  FOR EACH ROW
BEGIN
  SELECT users_sequence.nextval
  INTO :new.id
  FROM dual;
END;