CREATE TABLE posts (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       author_id BIGINT NOT NULL REFERENCES users(id),
                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                       updated_at TIMESTAMP NOT NULL DEFAULT now()
);