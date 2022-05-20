import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.controllers.UserController;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class UserServiceTest {
    private User user = new User();
    private User friend = new User();

    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService();

    @BeforeEach
    void createUser() {
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2020, 01, 01));
        user.setEmail("test@test");
        user.setId(1);

        friend.setName("Friend");
        friend.setLogin("Friend");
        friend.setBirthday(LocalDate.of(2020, 01, 01));
        friend.setEmail("test_friend@test");
        friend.setId(2);
    }

    @Test
    void shouldAddFriend() {
        inMemoryUserStorage.getUsers().put(user.getId(), user);
        inMemoryUserStorage.getUsers().put(user.getId(), friend);

        userService.addFriend(user, friend.getId());

        assertEquals(2L, user.getFriends().stream().collect(Collectors.toList()).get(0));
    }

    @Test
    void shouldReturnFriendsList() {
        inMemoryUserStorage.getUsers().put(user.getId(), user);
        inMemoryUserStorage.getUsers().put(user.getId(), friend);

        userService.addFriend(user, friend.getId());

        List<Long> friendsList = List.copyOf(user.getFriends());

        assertEquals(2L, friendsList.get(0));
    }

    @Test
    void shouldRemoveFriend() {
        inMemoryUserStorage.getUsers().put(user.getId(), user);
        inMemoryUserStorage.getUsers().put(user.getId(), friend);

        userService.addFriend(user, friend.getId());

        userService.deleteFriend(user, friend.getId());

        assertEquals(0, user.getFriends().size());
    }
}

