package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user, long userId);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();
}
