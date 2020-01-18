CREATE TABLE trackrequest (
    id INTEGER,
    requester INTEGER,
    requested INTEGER
);

ALTER TABLE trackrequest
  ADD (
    CONSTRAINT trackrequest_pk PRIMARY KEY (id)
  );
  
CREATE SEQUENCE trackrequest_sequence;

CREATE OR REPLACE TRIGGER trackrequest_on_insert
  BEFORE INSERT ON trackrequest
  FOR EACH ROW
BEGIN
  SELECT trackrequest_sequence.nextval
  INTO :new.id
  FROM dual;
END;