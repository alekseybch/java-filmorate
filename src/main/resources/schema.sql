DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS movies_genres CASCADE;
DROP TABLE IF EXISTS movies_likes CASCADE;
DROP TABLE IF EXISTS movies_directors CASCADE;
DROP TABLE IF EXISTS mpa_ratings CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS reviews_likes CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    user_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    email varchar(254) NOT NULL,
    login varchar(20) NOT NULL,
    user_name varchar(70) NOT NULL,
    birthday date NOT NULL
    CONSTRAINT valid_login CHECK (login <> ' ')
);

CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    mpa_name varchar(5) NOT NULL,
    mpa_description varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    film_name varchar(150) NOT NULL,
    description varchar(200),
    release_date date,
    duration int,
    rating int,
    mpa_id int REFERENCES mpa_ratings(mpa_id)
    CONSTRAINT valid_duration CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    genre_name varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    director_name varchar(70) NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    content varchar(200) NOT NULL,
    is_positive boolean,
    user_id int REFERENCES users (user_id) ON DELETE CASCADE,
    film_id int REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
    review_id int REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id int REFERENCES users (user_id) ON DELETE CASCADE,
    is_like boolean NOT NULL,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS friendships
(
    user_id int REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id int REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT validate_request CHECK (user_id <> friend_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS movies_likes
(
    film_id int REFERENCES films(film_id) ON DELETE CASCADE,
    user_id int REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS movies_genres
(
    film_id int REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id int REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS movies_directors
(
    film_id int REFERENCES films(film_id) ON DELETE CASCADE,
    director_id int REFERENCES directors(director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);