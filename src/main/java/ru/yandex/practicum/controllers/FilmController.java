package ru.yandex.practicum.controllers;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.InMemoryFilmStorage;
import ru.yandex.practicum.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Data
@RestController
public class FilmController {
    InMemoryFilmStorage inMemoryFilmStorage;
    FilmService filmService;
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage,
                          FilmService filmService,
                          InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return inMemoryFilmStorage.getFilms();
    }

    @PostMapping(value = "/films")
    public void create(@Valid @RequestBody Film film) {
        inMemoryFilmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public void edit(@RequestBody Film film) {
        inMemoryFilmStorage.edit(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void setLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.setLike(inMemoryUserStorage.getUserById(userId), inMemoryFilmStorage.getFilmById(filmId));
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getPopularFilm(inMemoryFilmStorage.getFilms(), count);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable("userId") Long userId) {
        filmService.removeLike(inMemoryUserStorage.getUserById(userId), inMemoryFilmStorage.getFilmById(filmId));
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilm(@PathVariable long id) {
        inMemoryFilmStorage.deleteFilm(id);
    }
}
