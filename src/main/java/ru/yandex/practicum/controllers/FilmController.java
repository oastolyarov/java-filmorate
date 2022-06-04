package ru.yandex.practicum.controllers;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.dao.FilmDbStorage;
import ru.yandex.practicum.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.FilmDbService;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.InMemoryFilmStorage;
import ru.yandex.practicum.storage.InMemoryUserStorage;
import ru.yandex.practicum.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Data
@RestController
public class FilmController {
    FilmStorage filmStorage;
    UserStorage userStorage;

    FilmDbService filmDbService;

    @Autowired
    public FilmController(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                          @Qualifier("UserDbStorage") UserStorage userStorage,
                          FilmDbService filmDbService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmDbService = filmDbService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        filmStorage.create(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film edit(@RequestBody Film film) {
        filmStorage.edit(film);
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void setLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmDbService.setLike(userId, filmId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmDbService.getPopularFilm(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmStorage.getFilmById(id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        filmDbService.removeLike(userId, filmId);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilm(@PathVariable long id) {
        filmStorage.deleteFilm(id);
    }
}
