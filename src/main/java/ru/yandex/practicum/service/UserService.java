package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    public void addFriend(User user, Long friendId) {
        user.getFriends().add(friendId);
    }

    public Set<Long> getFriendList(User user) {
        return user.getFriends();
    }

    public void deleteFriend(User user, Long friendId) {
        user.getFriends().remove(friendId);
    }

    public List<Long> commonListOfFriends(User user1, User user2) {
        List<Long> commonFriendsId = new ArrayList<>();

        List<Long> list1 = List.copyOf(user1.getFriends());
        List<Long> list2 = List.copyOf(user2.getFriends());

        for (Long userId : list1) {
            for (Long id : list2) {
                if (userId == id) {
                    commonFriendsId.add(userId);
                    break;
                }
            }
        } return commonFriendsId;
    }
}
