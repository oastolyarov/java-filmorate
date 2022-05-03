package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;

import java.util.Collection;

public interface UserStorage {

    public Collection<User> findAll();

    public User create(User user);

    public User change(User user);

    public User getUserById(Long userID);

    public void deleteUser(Long id);
}
