# Documentation for Backend

## Endpoints
### POST /api/drop
CREATE TABLE drops (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username char(100),
    force REAL,
    time TIMESTAMP,
    device_type char(30),
    orientation char(50)
);

### GET /api/drops


CREATE TABLE drops (
    id  INTEGER,
    username char(100),
    force REAL,
    time TIMESTAMP,
    device_type char(30),
    orientation char(50),
    device_id INTEGER
);

ALTER TABLE drops
  ADD (
    CONSTRAINT drops_pk PRIMARY KEY (id)
  );
  
CREATE SEQUENCE drops_sequence;

CREATE OR REPLACE TRIGGER drops_on_insert
  BEFORE INSERT ON drops
  FOR EACH ROW
BEGIN
  SELECT drops_sequence.nextval
  INTO :new.id
  FROM dual;
END;


## User
### POST /api/user
  body: {
    username: char(100),
    password: char(100),
    email: CHAR(100),
    token: char(??),
    token_expiration: TIMESTAMP,
    lat: REAL,
    lon: REAL
  }

### POST /api/user/login
  body: {
    username: char(100),
    password: char(100)
  }

  return {
    Token,
    expiration_time
  }

### GET /api/user/[user_id]/track
  body: {
    Authentication: Token [token],
    username: [user_to_track]
  }

  return {
    lat: []
    lon: []
  }

### GET /api/user/[user_id]/friend
  body: {
    Authentication: Token [token],
    username: [user_to_add]
  }

### POST /api/user/loc
  body: {
      Authentication: Token [token],
      lat: ,
      lon: 
  }

###
