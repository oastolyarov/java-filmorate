package ru.yandex.practicum.controllers;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.exceptions.UserIdNotValidException;
import ru.yandex.practicum.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Data
@RestController
@RequestMapping("/users")
public class UserController {

    UserStorage userStorage;

    @Autowired
    public UserController(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User change(@Valid @RequestBody User user) {
        return userStorage.change(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userID, @PathVariable("friendId") Long friendID) {
        User user = userStorage.getUserById(userID).get();
        User friend = userStorage.getUserById(friendID).get();

        if (user == null || friend == null) {
            Long noId = user == null ? userID : friendID; // проверка чей именно id отсутствует
            throw new UserIdNotValidException(String.format("Пользователь с id %d не существует.", noId));
        }

        userStorage.addFriend(userID, friendID); // добавляет пользователю друга
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userStorage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable Long id) {
        return userStorage.getFriendList(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userID, @PathVariable("friendId") Long friendID) {
        userStorage.deleteFriend(userID, friendID);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherUserId) {
        List<User> commonFriends = userStorage.commonListOfFriends(userId, otherUserId); // получаю массив с id общих друзей

        return commonFriends;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userStorage.deleteUser(id);
    }
}
