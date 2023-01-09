package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user, long userId);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();
}
