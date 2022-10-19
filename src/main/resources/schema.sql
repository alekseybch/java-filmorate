DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS movies_genres CASCADE;
DROP TABLE IF EXISTS movies_likes CASCADE;
DROP TABLE IF EXISTS mpa_ratings CASCADE;
DROP TABLE IF EXISTS genres CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    user_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    email varchar(254) NOT NULL,
    login varchar(20) NOT NULL,
    user_name varchar(70) NOT NULL,
    birthday date NOT NULL
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
    release_date date NOT NULL,
    duration int,
    rating int,
    mpa_id int REFERENCES mpa_ratings(mpa_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    genre_name varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships
(
    user_id int REFERENCES users(user_id) NOT NULL,
    friend_id int REFERENCES users(user_id) NOT NULL,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS movies_likes
(
    film_id int REFERENCES films(film_id) NOT NULL,
    user_id int REFERENCES users(user_id) NOT NULL,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS movies_genres
(
    film_id int REFERENCES films(film_id) NOT NULL,
    genre_id int REFERENCES genres(genre_id) NOT NULL,
    PRIMARY KEY (film_id, genre_id)
);