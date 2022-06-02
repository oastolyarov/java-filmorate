package ru.yandex.practicum.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.FilmNotExistsException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.MpaGetter;
import ru.yandex.practicum.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM FILM";

        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> getFilm(rs)));
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId());

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM film WHERE name = ? ORDER BY FILM_ID DESC",
                film.getName());
        if (sqlRowSet.next()) {
            film.setId(sqlRowSet.getInt("film_id"));
        } // присваиваю фильму id

        return film;
    }

    @Override
    public void edit(Film film) {
        if (filmCheck(film.getId())) {
            String sqlFilm = "UPDATE FILM SET " +
                    "NAME = ?," +
                    "DESCRIPTION = ?," +
                    "RELEASE_DATE = ?," +
                    "DURATION = ?," +
                    "RATE = ?," +
                    "MPA_ID = ?";

            jdbcTemplate.update(sqlFilm,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getRate(),
                    film.getMpa().getId());
        }
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT * FROM FILM WHERE film_id = ?";
        Film film = new Film();
        try {
            film = jdbcTemplate.query(sql, (rs, rowNum) -> getFilm(rs), id).get(0);
            return film;
        } catch (FilmNotExistsException | IndexOutOfBoundsException e) {
            throw new FilmNotExistsException(String.format("Фильм с id %d не найден.", id));
        }
    }

    @Override
    public void deleteFilm(Long id) {
        String sqlFilm = "DELETE FROM FILM WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlFilm,
                id);
    }

    @Override
    public void setLike(Long userId, Long filmId) {
        userDbStorage.userCheck(userId);
        filmCheck(filmId);

        String sqlInsert = "INSERT INTO LIKES (FILM_ID, USER_ID)" +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlInsert,
                filmId, userId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT FILM_ID, COUNT(DISTINCT USER_ID) AS LIKES FROM LIKES GROUP BY FILM_ID");

        List<Film> films = findAll(); // получаю список всех фильмов

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

    @Override
    public void removeLike(Long userId, Long filmId) {
        userDbStorage.userCheck(userId);
        filmCheck(filmId);

        String sqlDeleteLike = "DELETE FROM LIKES " +
                               "WHERE USER_ID = ? AND FILM_ID = ?";

        jdbcTemplate.update(sqlDeleteLike,
                userId, filmId);
    }

    private Film getFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));

        // джойню рейтинг
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM MPA WHERE MPA_ID = ?",
                rs.getInt("mpa_id"));
        MpaGetter mpaGetter = new MpaGetter();
        while (sqlRowSet.next()) {
            mpaGetter.setId(sqlRowSet.getInt("mpa_id"));
            mpaGetter.setName(sqlRowSet.getString("name"));
        }

        // джойню лайки
        SqlRowSet sqlRowSet1 = jdbcTemplate.queryForRowSet("SELECT COUNT(DISTINCT USER_ID) AS LIKES FROM LIKES WHERE FILM_ID = ?",
                rs.getInt("film_id"));

        if (sqlRowSet1.next()) {
            film.setLikes(sqlRowSet1.getLong("LIKES"));
        }

        film.setMpa(mpaGetter);

        return film;
    }

    private boolean filmCheck(Long id) {
        if (getFilmById(id) != null) {
            return true;
        } else {
            throw new FilmNotExistsException(String.format("Фильм с id %d не найден", id));
        }
    }
}
