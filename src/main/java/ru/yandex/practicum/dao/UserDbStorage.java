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

    @Override
    public void addFriend(Long userId, Long friendId) {
        userCheck(userId); // проверка юзера на наличие
        userCheck(friendId); // проверка юзера на наличие

        String sqlQuery = "INSERT INTO FRIENDS (user_id, friend_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                userId,
                friendId);
    }

    @Override
    public List<User> getFriendList(Long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT friend_id FROM FRIENDS " +
                "where USER_ID = ?", id);
        List<User> friends = new ArrayList<>();
        while (sqlRowSet.next()) {
            User friend = getUserById(sqlRowSet.getLong("friend_id")).get();

            friends.add(friend);
        }
        return friends;
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userCheck(userId); // проверка юзера на наличие
        userCheck(friendId); // проверка юзера на наличие

        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? and FRIEND_ID = ?";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> commonListOfFriends(Long userId, Long otherUserId) {
        userCheck(userId);
        userCheck(otherUserId);

        List<Long> firstUser = new ArrayList<>();
        List<Long> secondUser = new ArrayList<>();

        SqlRowSet firstUserSql = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID from FRIENDS " +
                "WHERE user_id = ?", userId);
        SqlRowSet secondUserSql = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID from FRIENDS " +
                "WHERE user_id = ?", otherUserId);

        while (firstUserSql.next()) {
            firstUser.add(firstUserSql.getLong("friend_id"));
        }

        while (secondUserSql.next()) {
            secondUser.add(secondUserSql.getLong("friend_id"));
        }

        firstUser.retainAll(secondUser);


        return firstUser.stream().map(s -> getUserById(s).get()).collect(Collectors.toList());
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
