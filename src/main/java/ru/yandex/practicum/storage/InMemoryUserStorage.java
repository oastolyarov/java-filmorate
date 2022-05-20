package ru.yandex.practicum.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.UserIdNotValidException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.User;

import java.util.*;

@Slf4j
@Component
@Getter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static long id = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, вместо имени будет использован логин.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Не валидный email.");
        }

        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Создан пользователь {}", user, toString());
        return user;
    }

    @Override
    public User change(User user) {
        if (user.getName() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }

        if (user.getId() <= 0) {
            throw new UserIdNotValidException("Id пользователя не может быть отрицательным или равным нулю.");
        }
        users.put(user.getId(), user);
        log.info("Пользователь " + user.getEmail() + " изменен.");

        return user;
    }

    @Override
    public User getUserById(Long userID) {
        if (userID < 0) {
            throw new UserIdNotValidException("ID пользователя не может быть отрицательным.");
        }

        User user;
        for (Long l : users.keySet()) {
            if(users.get(l).getId() == userID) {
                user = users.get(l);
                return user;
            }
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(getUserById(id));
    }
}
