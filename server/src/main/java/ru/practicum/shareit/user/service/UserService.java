package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(User user);

    UserResponse updateUser(User user, Long userId);

    void deleteUser(Long userId);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();
}