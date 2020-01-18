CREATE TABLE track (
    id INTEGER,
    tracker INTEGER,
    tracked INTEGER
);

ALTER TABLE track
  ADD (
    CONSTRAINT track_pk PRIMARY KEY (id)
  );
  
CREATE SEQUENCE track_sequence;

CREATE OR REPLACE TRIGGER track_on_insert
  BEFORE INSERT ON track
  FOR EACH ROW
BEGIN
  SELECT track_sequence.nextval
  INTO :new.id
  FROM dual;
END;