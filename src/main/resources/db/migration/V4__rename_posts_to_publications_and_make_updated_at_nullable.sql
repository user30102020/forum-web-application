ALTER TABLE posts RENAME TO publications;

ALTER TABLE publications ALTER COLUMN updated_at DROP NOT NULL;
ALTER TABLE publications ALTER COLUMN updated_at DROP DEFAULT;