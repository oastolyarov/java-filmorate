package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.util.List;

public interface FilmStorage {

    public List<Film> findAll();

    public Film create(Film film);

    public void edit(Film film);

    public Film getFilmById(Long id);

    public void deleteFilm(Long id);

    public void setLike(Long userId, Long filmId);

    public List<Film> getPopularFilm(int count);

    public void removeLike(Long userId, Long filmId);
}
