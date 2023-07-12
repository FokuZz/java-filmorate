CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(34) NOT NULL,
    login    VARCHAR(24) NOT NULL,
    name     VARCHAR(24),
    birthday DATE        NOT NULL
);

CREATE TABLE IF NOT EXISTS rating
(
    rating_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      INTEGER AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(32)  NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE,
    duration     INTEGER      NOT NULL,
    rate         INTEGER,
    rating_id    INTEGER REFERENCES rating (rating_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS likes
(
    id      INTEGER AUTO_INCREMENT PRIMARY KEY,
    film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    id               INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id          INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id        INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    confirmed_friend BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(32) NOT NULL
);


CREATE TABLE IF NOT EXISTS film_genre
(
    id       INTEGER AUTO_INCREMENT PRIMARY KEY,
    film_id  INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre (genre_id) ON DELETE RESTRICT
);