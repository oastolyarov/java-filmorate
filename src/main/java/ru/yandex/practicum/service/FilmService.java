package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    public void setLike(User user, Film film) {
        film.getLikes().add(user.getId());
    }

    public List<Film> getPopularFilm(List<Film> films, int count) {
        return films.stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count).collect(Collectors.toList());
    }

    public void removeLike(User user, Film film) {
        film.getLikes().remove(user.getId());
    }
}
