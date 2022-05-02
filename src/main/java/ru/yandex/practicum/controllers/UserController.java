package ru.yandex.practicum.controllers;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.exceptions.UserIdNotValidException;
import ru.yandex.practicum.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.*;

@Data
@RestController
@RequestMapping("/users")
public class UserController {

    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User change(@Valid @RequestBody User user) {
        return inMemoryUserStorage.change(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userID, @PathVariable("friendId") Long friendID) {
        User user = inMemoryUserStorage.UserById(userID);
        User friend = inMemoryUserStorage.UserById(friendID);

        if (user == null || friend == null) {
            Long noId = user == null ? userID : friendID; // проверка чей именно id отсутствует
            throw new UserIdNotValidException(String.format("Пользователь с id %d не существует.", userID));
        }

        userService.addFriend(user, friendID); // добавляет пользователю друга
        userService.addFriend(friend, userID); // добавляет этому другу в друзья пользователя
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return inMemoryUserStorage.UserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable Long id) {
        User user = inMemoryUserStorage.UserById(id);

        List<Long> listId = List.copyOf(userService.getFriendList(user));
        List<User> friendList = new ArrayList<>();

        for (int i = 0; i < listId.size(); i++) {
            friendList.add(inMemoryUserStorage.UserById(listId.get(i)));
        }

        return friendList;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userID, @PathVariable("friendId") Long friendID) {
        User user = inMemoryUserStorage.UserById(userID);
        User friend = inMemoryUserStorage.UserById(friendID);

        if (user == null || friend == null) {
            Long noId = user == null ? userID : friendID; // проверка чей именно id отсутствует
            throw new UserIdNotValidException(String.format("Пользователь с id %d не существует.", userID));
        }

        userService.deleteFriend(user, friendID); // удаляет у пользователя друга
        userService.deleteFriend(friend, userID); // удаляет у друга пользователя
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherUserId) {

        List<Long> idCommonFriend = userService.commonListOfFriends(inMemoryUserStorage.UserById(userId),
                inMemoryUserStorage.UserById(otherUserId)); // получаю массив с id общих друзей

        List<User> commonFriends = new ArrayList<>();

        for (Long l : idCommonFriend) { // ищу по этому id пользователя
            commonFriends.add(inMemoryUserStorage.UserById(l));
        }

        return commonFriends;
    }
}
