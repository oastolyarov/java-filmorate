package ru.yandex.practicum.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.service.UserDbService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbServiceTest {

    private final UserDbService userDbService;

    @Test
    public void addFriend() {
        userDbService.addFriend(1L, 2L);

        long listFriends = userDbService.getFriendList(1L).get(0).getId();

        assertThat(listFriends).isEqualTo(2L);
    }

    @Test
    public void getFriendList() {
        userDbService.addFriend(1L, 2L);

        int friendsList = userDbService.getFriendList(1L).size();

        assertThat(friendsList).isEqualTo(1);

    }

    @Test
    public void deleteFriend() {
        userDbService.deleteFriend(1L, 2L);

        int friendsList = userDbService.getFriendList(1L).size();

        assertThat(friendsList).isEqualTo(0);
    }
}
