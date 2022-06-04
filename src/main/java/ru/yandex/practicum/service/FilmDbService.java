package ru.yandex.practicum.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.FilmDbStorage;
import ru.yandex.practicum.dao.UserDbStorage;
import ru.yandex.practicum.model.Film;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmDbService {

    private UserDbStorage userDbStorage;
    private FilmDbStorage filmDbStorage;
    private JdbcTemplate jdbcTemplate;

    FilmDbService(UserDbStorage userDbStorage,
                  FilmDbStorage filmDbStorage,
                  JdbcTemplate jdbcTemplate) {
        this.filmDbStorage = filmDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    public void setLike(Long userId, Long filmId) {
        userDbStorage.userCheck(userId);
        filmDbStorage.filmCheck(filmId);

        String sqlInsert = "INSERT INTO LIKES (FILM_ID, USER_ID)" +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlInsert,
                filmId, userId);
    }

    public List<Film> getPopularFilm(int count) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT FILM_ID, COUNT(DISTINCT USER_ID) AS LIKES FROM LIKES GROUP BY FILM_ID");

        List<Film> films = filmDbStorage.findAll(); // получаю список всех фильмов

        // добавляю лайки к тем фильмам, у которых они есть
        while (sqlRowSet.next()) {
            for (Film film : films) {
                if (film.getId() == sqlRowSet.getInt("film_id")) {
                    film.setLikes(sqlRowSet.getLong("LIKES"));
                }
            }
        }

        return films.stream()
                .sorted((o1, o2) -> (int) (o2.getLikes() - o1.getLikes()))
                .limit(count).collect(Collectors.toList());
    }

    public void removeLike(Long userId, Long filmId) {
        userDbStorage.userCheck(userId);
        filmDbStorage.filmCheck(filmId);

        String sqlDeleteLike = "DELETE FROM LIKES " +
                "WHERE USER_ID = ? AND FILM_ID = ?";

        jdbcTemplate.update(sqlDeleteLike,
                userId, filmId);
    }
}
