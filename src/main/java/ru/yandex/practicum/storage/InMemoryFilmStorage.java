package ru.yandex.practicum.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.FilmNotExistsException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private static int id = 1;

    @Override
    public List<Film> findAll() {
        return films;
    }

    @Override
    public void create(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.info("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
            log.info("Максимальная длина описания - 200 символов.");
            throw new ValidationException("Длина описания должна быть в пределах от 1 до 200 символов.");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза не может быть раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }

        if (film.getDuration().toMinutes() < 0) {
            log.info("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }

        // преобразую переданное значение в минуты
        film.setDuration(Duration.ofMinutes(film.getDuration().toSeconds()));
        film.setId(id);
        id++;
        films.add(film);
        log.info("Добавлен фильм {}", film.toString());
    }

    @Override
    public void edit(Film film) {
        if (films.size() > 0) {
            Film delFilm = films.stream().filter(s -> s.getId() == film.getId()).collect(Collectors.toList()).get(0);
            films.remove(delFilm);
        }
        films.add(film);
        log.info("Обновлен фильм {}", film.toString());
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = new Film();
        if (films.isEmpty() || films == null) {
            throw new FilmNotExistsException("В списке нет еще ни одного фильма.");
        }

        try {
            film = films.stream().filter(s -> s.getId() == id).collect(Collectors.toList()).get(0);
        } catch (RuntimeException e) {
            throw new FilmNotExistsException("Фильм с id " + id + " не найден.");
        }

        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(getFilmById(id));
    }
}
