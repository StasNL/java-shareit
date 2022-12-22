package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}
