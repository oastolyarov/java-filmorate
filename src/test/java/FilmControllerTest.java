import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.controllers.FilmController;
import ru.yandex.practicum.dao.FilmDbStorage;
import ru.yandex.practicum.dao.UserDbStorage;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.InMemoryFilmStorage;
import ru.yandex.practicum.storage.InMemoryUserStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private Film film = new Film();

    private JdbcTemplate jdbcTemplate;

    UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
    FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, userDbStorage);

    @BeforeEach
    void newFilm() {
        film = new Film();
        film.setDuration(90);
        film.setDescription("Описание фильма");
        film.setName("Название");
        film.setReleaseDate(LocalDate.of(2020, 01, 01));
    }

    @Test
    void shouldNotCreateFilmIfNameIsEmpty() {
        film.setName("");

        FilmController filmController = new FilmController(filmStorage, userDbStorage);
        String message = null;

        try {
            filmController.create(film);
        } catch (ValidationException e) {
            message = e.getMessage();
        }

        assertEquals(message, "Название фильма не может быть пустым");
    }

    @Test
    void shouldNotCreateFilmIfDescriptionIsTooLong() {
        // тут 201 символ
        film.setDescription("123456789123456789123456789123456789123456789123456789123456789" +
                "123456789123456789123456789123456789123456789123456789123456789123456789" +
                "123456789123456789123456789123456789123456789123456789123456789123");

        FilmController filmController = new FilmController(filmStorage, userDbStorage);
        String message = null;

        try {
            filmController.create(film);
        } catch (ValidationException e) {
            message = e.getMessage();
        }

        assertEquals(message, "Длина описания должна быть в пределах от 1 до 200 символов.");
    }

    @Test
    void shouldCreateFilmIfDescriptionIs200() {
        // тут 200 символов
        film.setDescription("123456789123456789123456789123456789123456789123456789123456789" +
                "123456789123456789123456789123456789123456789123456789123456789123456789" +
                "12345678912345678912345678912345678912345678912345678912345678912");

        FilmController filmController = new FilmController(filmStorage, userDbStorage);

        filmController.create(film);

        assertEquals(filmStorage.findAll().size(), 1);
    }

    @Test
    void shouldNotCreateFilmWhileDateIsTooOld() {
        film.setReleaseDate(LocalDate.of(1800, 01, 01));

        FilmController filmController = new FilmController(filmStorage, userDbStorage);
        String message = null;

        try {
            filmController.create(film);
        } catch (ValidationException e) {
            message = e.getMessage();
        }

        assertEquals(message, "Дата релиза не может быть раньше 28 декабря 1895 года.");
    }

    @Test
    void shouldCreateFilmIfDateIs28_12_1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        FilmController filmController = new FilmController(filmStorage, userDbStorage);

        filmController.create(film);

        assertEquals(filmStorage.findAll().size(), 1);
    }

    @Test
    void shouldNotCreateFilmWhileDurationLess0() {
        film.setDuration(-5);

        FilmController filmController = new FilmController(filmStorage, userDbStorage);
        String message = null;

        try {
            filmController.create(film);
        } catch (ValidationException e) {
            message = e.getMessage();
        }

        assertEquals(message, "Продолжительность фильма должна быть положительной.");
    }

    @Test
    void shouldCreateFilmIfDurationIs0() {
        film.setDuration(0);

        FilmController filmController = new FilmController(filmStorage, userDbStorage);

        filmController.create(film);

        assertEquals(filmStorage.findAll().size(), 1);
    }
}
