package ru.yandex.practicum.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmDbService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbServiceTest {

    private final FilmDbService filmDbService;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void setLike() {
        filmDbService.setLike(1L, 1L);

        Film film = filmDbStorage.getFilmById(1L);

        assertThat(film.getLikes()).isEqualTo(1);
    }

    @Test
    public void getPopularFilm() {
        Film film = filmDbService.getPopularFilm(1).get(0);

        assertThat(film.getLikes()).isEqualTo(1);
    }

    @Test
    public void removeLike() {
        filmDbService.removeLike(2L, 1L);

        Film film = filmDbStorage.getFilmById(1L);

        assertThat(film.getLikes()).isEqualTo(0);
    }
}
