-- Migration: Alter profile_image_url column from VARCHAR(255) to TEXT
-- Required to store base64-encoded profile images

ALTER TABLE users ALTER COLUMN profile_image_url TYPE TEXT;