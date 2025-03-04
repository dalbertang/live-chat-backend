-- Create the users table (postgresql)
CREATE TABLE IF NOT EXISTS users (
   id VARCHAR(255) PRIMARY KEY,
   email VARCHAR(255) UNIQUE NOT NULL,
   name VARCHAR(255),
   roles TEXT[] NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
  id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid(), -- Unique ID for each message
  sender VARCHAR(255) NOT NULL, -- Sender's name or ID
  content TEXT NOT NULL, -- Message content
  timestamp TIMESTAMPTZ DEFAULT NOW(), -- Message timestamp
  parent_id VARCHAR(255) REFERENCES messages(id) ON DELETE CASCADE -- References parent message for threads
);

-- Index for faster retrieval of top-level messages
CREATE INDEX IF NOT EXISTS idx_parent_id ON messages (parent_id);

