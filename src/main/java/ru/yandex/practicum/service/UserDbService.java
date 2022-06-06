package ru.yandex.practicum.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.UserDbStorage;
import ru.yandex.practicum.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDbService {

    UserDbStorage userDbStorage;
    JdbcTemplate jdbcTemplate;

    UserDbService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(Long userId, Long friendId) {
        userDbStorage.userCheck(userId); // проверка юзера на наличие
        userDbStorage.userCheck(friendId); // проверка юзера на наличие

        String sqlQuery = "INSERT INTO FRIENDS (user_id, friend_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                userId,
                friendId);
    }

    public List<User> getFriendList(Long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT friend_id FROM FRIENDS " +
                "where USER_ID = ?", id);
        List<User> friends = new ArrayList<>();
        while (sqlRowSet.next()) {
            User friend = userDbStorage.getUserById(sqlRowSet.getLong("friend_id")).get();

            friends.add(friend);
        }
        return friends;
    }

    public void deleteFriend(Long userId, Long friendId) {
        userDbStorage.userCheck(userId); // проверка юзера на наличие
        userDbStorage.userCheck(friendId); // проверка юзера на наличие

        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? and FRIEND_ID = ?";

        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> commonListOfFriends(Long userId, Long otherUserId) {
        userDbStorage.userCheck(userId);
        userDbStorage.userCheck(otherUserId);

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


        return firstUser.stream().map(s -> userDbStorage.getUserById(s).get()).collect(Collectors.toList());
    }
}
