package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid UserCreate userCreate) {
        User user = UserMapper.userCreateToUser(userCreate);
        user = userService.createUser(user);
        return UserMapper.userToUserResponse(user);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@RequestBody @Valid UserUpdate userUpdate,
                                   @PathVariable Long userId) {
        User user = UserMapper.userUpdateToUser(userUpdate, userId);
        user = userService.updateUser(user);
        return UserMapper.userToUserResponse(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public @Valid UserResponse getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return UserMapper.userToUserResponse(user);
    }

    @GetMapping
    public List<@Valid UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::userToUserResponse)
                .collect(Collectors.toList());
    }
}
