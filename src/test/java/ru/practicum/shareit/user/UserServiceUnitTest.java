package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.utils.PreparingForUnitTest;

import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest extends PreparingForUnitTest {
    @Test
    void updateUserTest() {
        User userToUpdate = createDefaultUser();
        userToUpdate.setName("updatedUser");
        User userFromDb = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userFromDb));

        when(userRepository.save(Mockito.any()))
                .thenReturn(userFromDb);

        Long userId = userToUpdate.getId();
        UserResponse userAfterUpdate = userService.updateUser(userToUpdate, userId);
        UserResponse userToUpdateResp = UserMapper.userToUserResponse(userToUpdate);

        assertEquals(userToUpdateResp, userAfterUpdate);
    }

    @Test
    void deleteUserTest() {
        User user = createDefaultUser();
        long userId = user.getId();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void getUserByIdTest() {
        User user = createDefaultUser();
        long userId = user.getId();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserResponse userResponseForTest = UserMapper.userToUserResponse(user);
        UserResponse userResponse = userService.getUserById(userId);

        assertEquals(userResponseForTest, userResponse);
    }

    @Test
    void getAllUsersTest() {
        User user = createDefaultUser();
        List<User> users = List.of(user);
        when(userRepository.findAll())
                .thenReturn(users);
        List<UserResponse> userResponses = userService.getAllUsers();
        List<UserResponse> userResponsesForTest = UserMapper.userToUserResponse(users);

        assertEquals(userResponsesForTest, userResponses);
    }
}