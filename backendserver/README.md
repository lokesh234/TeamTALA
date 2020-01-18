# Documentation for Backend

## Endpoints
### POST /api/drop
CREATE TABLE drops (
    id INTEGER PRIMARY KEY,
    username char(100),
    force REAL,
    time TIMESTAMP,
    device_type char(30),
    orientation char(50)
);

### GET /api/drops/[device_id]
### GET /api/drop/[drop_id