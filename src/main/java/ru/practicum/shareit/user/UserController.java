package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid User user) {
        user = userService.createUser(user);
        user = userService.getUserById(user.getId());
        return UserMapper.userToUserResponse(user);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@RequestBody @Valid User user,
                                   @PathVariable Long userId) {
        user = userService.updateUser(user, userId);
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
