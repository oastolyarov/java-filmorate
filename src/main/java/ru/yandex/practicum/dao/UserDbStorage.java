package ru.yandex.practicum.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.UserIdNotValidException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> getUser(rs)));
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)" +
                "VALUES (?, ?, ?, ?)";



        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                ((user.getName() == null || user.getName() == "") ? user.getLogin() : user.getName()),
                user.getBirthday());

        String userRow = "SELECT * FROM users WHERE EMAIL = ? ORDER BY USER_ID DESC";

        return jdbcTemplate.query(userRow, (rs, rowNum) -> getUser(rs), user.getEmail()).get(0);
    }

    @Override
    public User change(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, вместо имени будет использован логин.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Не валидный email.");
        }

        if (user.getId() < 0) {
            throw new UserIdNotValidException("ID пользователя не может быть отрицательным.");
        }

        String sqlQuery = "UPDATE USERS  SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ?" +
                "WHERE USER_ID = ?";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userID) {
        User user = new User();
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE USER_ID = ?", userID);

        if (userID < 0) {
            throw new UserIdNotValidException("ID пользователя не может быть отрицательным.");
        }

        if (sqlRow.next()) {

            user.setId(sqlRow.getInt("user_id"));
            user.setEmail(sqlRow.getString("email"));
            user.setLogin(sqlRow.getString("login"));
            user.setName(sqlRow.getString("name"));
            user.setBirthday(sqlRow.getDate("birthday").toLocalDate());

            log.info("Найден пользователь: {}", user.getId(), user.getLogin());

            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userID);
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(Long id) {
        String sqlQuery = ("DELETE FROM USERS WHERE user_id = ?");

        jdbcTemplate.update(sqlQuery, id);
    }

    private User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }

    public boolean userCheck(Long id) {
        if (getUserById(id).isPresent()) {
            return true;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new UserIdNotValidException(String.format("Пользователь с id %d не существует.", id));
        }
    }
}
