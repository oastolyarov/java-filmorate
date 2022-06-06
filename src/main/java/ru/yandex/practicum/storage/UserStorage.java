package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;

import java.util.*;

public interface UserStorage {

    public Collection<User> findAll();

    public User create(User user);

    public User change(User user);

    public Optional<User> getUserById(Long userID);

    public void deleteUser(Long id);
}
