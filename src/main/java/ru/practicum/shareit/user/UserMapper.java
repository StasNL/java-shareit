package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;

public class UserMapper {
    public static User userCreateToUser(UserCreate userCreate) {
        return User.builder()
                .name(userCreate.getName())
                .email(userCreate.getEmail())
                .build();
    }

    public static User userUpdateToUser(UserUpdate userUpdate, Long userId) {
        return User.builder()
                .id(userId)
                .name(userUpdate.getName())
                .email(userUpdate.getEmail())
                .build();
    }

    public static UserResponse userToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
