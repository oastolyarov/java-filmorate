DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS LIKES CASCADE;
DROP TABLE IF EXISTS GENRES_FILM CASCADE;
DROP TABLE IF EXISTS FRIENDS CASCADE;

create table if not exists users
(
    user_id  int auto_increment unique,
    email    varchar,
    login    varchar,
    name     varchar,
    birthday date,
    primary key (user_id)
);

create table if not exists friends
(
    user_id   int references users (user_id),
    friend_id int references users (user_id)
);

create table if not exists mpa
(
    mpa_id      int auto_increment unique,
    name        varchar,
    description varchar,
    primary key (mpa_id)
);

create table if not exists film
(
    film_id      int auto_increment unique,
    name         varchar,
    description  varchar,
    release_date date,
    duration     int,
    rate int,
    mpa_id       int
);

create table if not exists genre
(
    genre_id int auto_increment unique,
    name     varchar,
    primary key (genre_id)
);

create table if not exists genres_film
(
    film_id  int references film (film_id),
    genre_id int references genre (genre_id)
);

create table if not exists likes
(
    film_id int references film (film_id),
    user_id int references users (user_id)
);

