package ru.yandex.practicum.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void findAll() {

        Optional<List<User>> optionalUsers = Optional.of(List.copyOf(userStorage.findAll()));

        assertThat(optionalUsers.get().size()).isEqualTo(3);

    }

    @Test
    public void create() {
        User user = new User();
        user.setId(3L);
        user.setName("test1");
        user.setEmail("test1@test1.ru");
        user.setLogin("test1");
        user.setBirthday(LocalDate.of(1999, 01, 01));

        userStorage.create(user);

        Optional<User> optionalUser = userStorage.getUserById(3L);

        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 3L));
    }

    @Test
    public void change() {

        User user1 = new User();
        user1.setId(2);
        user1.setName("test1Changed");
        user1.setEmail("test1Changed@test1.ru");
        user1.setLogin("test1Changed");
        user1.setBirthday(LocalDate.of(1998, 01, 01));
        userStorage.change(user1);

        Optional<User> optionalUser = userStorage.getUserById(2L);

        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(user2 ->
                        assertThat(user2).hasFieldOrPropertyWithValue("name", "test1Changed"));
    }

    @Test
    public void getUserById() {
        Optional<User> optionalUser = userStorage.getUserById(1L);

        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void deleteUser() {
        userStorage.deleteUser(3L);

        Optional<User> optionalUser = userStorage.getUserById(3L);

        assertThat(optionalUser)
                .isEmpty();
    }
}