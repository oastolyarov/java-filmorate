package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmStorage {

    public List<Film> findAll();

    public void create(Film film);

    public void edit(Film film);

    public Film getFilmById(Long id);
}
