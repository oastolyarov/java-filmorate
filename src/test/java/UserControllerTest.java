import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.controllers.UserController;
import ru.yandex.practicum.dao.UserDbStorage;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class UserControllerTest {
    private User user = new User();

    JdbcTemplate jdbcTemplate;

    UserStorage userStorage = new UserDbStorage(jdbcTemplate);

    @BeforeEach
    void createUser() {

        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2020, 01, 01));
        user.setEmail("test@test");
    }

    @Test
    void shouldNotCreateUserWhileEmailIsBlank() {
        user.setEmail("");

        UserController userController = new UserController(userStorage);
        String message = null;

        try {
            userController.create(user);
        } catch (ValidationException e) {
            message = e.getMessage();
        }

        assertEquals(message, "Не валидный email.");
    }

    @Test
    void nameShouldEqualsLoginIfNameIsEmpty() {
        user.setName("");

        UserController userController = new UserController(userStorage);

        userController.create(user);

        assertEquals(user.getName(), user.getLogin());
    }
}

