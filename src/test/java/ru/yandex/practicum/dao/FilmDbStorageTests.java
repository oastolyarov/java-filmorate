package ru.yandex.practicum.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.MpaGetter;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testFindFilmById() {
        Film film = filmDbStorage.getFilmById(1L);

        assertThat(film)
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void findAll() {
        List<Film> films = filmDbStorage.findAll();

        assertThat(films.size()).isEqualTo(2);
    }

    @Test
    public void create() {
        Film film = new Film();

        film.setName("Фильм1");
        film.setDescription("Описание фильма 1");
        film.setReleaseDate(LocalDate.of(2000, 01, 01));
        film.setDuration(100);
        film.setRate(10);

        MpaGetter mpaGetter = new MpaGetter();
        mpaGetter.setId(1);
        mpaGetter.setName("У фильма нет возрастных ограничений");

        film.setMpa(mpaGetter);

        filmDbStorage.create(film);

        assertThat(film).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    public void edit() {
        Film film = new Film();

        film.setId(2);
        film.setName("Фильм измененный");
        film.setDescription("Описание фильма измененного");
        film.setReleaseDate(LocalDate.of(2000, 01, 01));
        film.setDuration(100);
        film.setRate(10);

        MpaGetter mpaGetter = new MpaGetter();
        mpaGetter.setId(2);
        mpaGetter.setName("Детям рекомендуется смотреть фильм с родителями");

        film.setMpa(mpaGetter);

        filmDbStorage.edit(film);

        assertThat(film).hasFieldOrPropertyWithValue("description", "Описание фильма измененного");
    }

    @Test
    public void getFilmById() {
        Film film = filmDbStorage.getFilmById(1L);

        assertThat(film.getId()).isEqualTo(1L);
    }

    @Test
    public void deleteFilm() {
        filmDbStorage.deleteFilm(2L);

        List<Film> films = filmDbStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
    }

    @Test
    public void setLike() {
        filmDbStorage.setLike(1L, 1L);

        Film film = filmDbStorage.getFilmById(1L);

        assertThat(film.getLikes()).isEqualTo(1);
    }

    @Test
    public void getPopularFilm() {
        Film film = filmDbStorage.getPopularFilm(1).get(0);

        assertThat(film.getLikes()).isEqualTo(1);
    }

    @Test
    public void removeLike() {
        filmDbStorage.removeLike(2L, 1L);

        Film film = filmDbStorage.getFilmById(1L);

        assertThat(film.getLikes()).isEqualTo(0);
    }
}
