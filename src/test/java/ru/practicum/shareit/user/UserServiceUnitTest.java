package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.utils.PreparingForUnitTest;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;

import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest extends PreparingForUnitTest {

    @Test
    void createUserTest() {
        User user = createDefaultUser();
        // При передаче пользователя без почты выбрасывается исключение.
        user.setEmail(null);
        Throwable exception = assertThrows(BadRequestException.class, () -> userService.createUser(user));
        assertEquals("Графа email пуста", exception.getMessage());
    }

    @Test
    void updateUserTest() {
        User userToUpdate = createDefaultUser();
        userToUpdate.setName("updatedUser");
        User userFromDb = createDefaultUser();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userFromDb));

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(userFromDb);

        Long userId = userToUpdate.getId();
        UserResponse userAfterUpdate = userService.updateUser(userToUpdate, userId);
        UserResponse userToUpdateResp = UserMapper.userToUserResponse(userToUpdate);

        assertEquals(userToUpdateResp, userAfterUpdate);
    }
}
