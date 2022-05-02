package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    public void setLike(User user, Film film) {
        film.getLikes().add(user.getEmail());
    }

    public List<Film> getPopularFilm(List<Film> films, int count) {
        List<Film> top10Films = films.stream()
                .sorted((o1, o2) -> o1.getLikes().size() - o2.getLikes().size())
                .limit(count).collect(Collectors.toList());

        return top10Films;
    }

    public void removeLike(User user, Film film) {
        film.getLikes().remove(user.getEmail());
    }
}
