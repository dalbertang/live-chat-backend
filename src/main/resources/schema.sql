-- Create the users table (postgresql)
CREATE TABLE IF NOT EXISTS users (
   id VARCHAR(255) PRIMARY KEY,
   email VARCHAR(255) UNIQUE NOT NULL,
   name VARCHAR(255),
   roles TEXT[] NOT NULL
);
