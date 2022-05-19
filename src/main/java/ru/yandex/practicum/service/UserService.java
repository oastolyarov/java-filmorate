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
        Set<Long> setMutualFriendsUser1 = user1.getFriends();
        Set<Long> setMutualFriendsUser2 = user2.getFriends();

        /*
        почему-то функция "retainAll" ломала мне всю логику и
        очищала массив друзей, поэтому я использовал циклы
        я оставлю этот кусочек, чтобы потом разобраться с этим
        */

        //setMutualFriendsUser1.retainAll(setMutualFriendsUser2);

        List<Long> mutualFriends = new ArrayList<>();

        for (Long l : setMutualFriendsUser1) {
            for (Long s : setMutualFriendsUser2) {
                if (l == s) {
                    mutualFriends.add(l);
                }
            }
        }
        return mutualFriends;
    }
}
