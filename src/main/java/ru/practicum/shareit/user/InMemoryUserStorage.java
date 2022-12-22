package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;

import ru.practicum.shareit.exceptions.notfound.DuplicateException;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;

import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private long id;
    private static final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        checkEmail(user.getEmail());
        Long userId = ++id;
        user.setId(userId);
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
        Long userId = user.getId();
        checkUserById(userId);
        User userToUpdate = users.get(userId);

        String name = user.getName();
        String email = user.getEmail();
        if (email != null && !email.equalsIgnoreCase(userToUpdate.getEmail())) {
            checkEmail(email);
            userToUpdate.setEmail(email);
        }
        if (name != null)
            userToUpdate.setName(name);

        users.put(userToUpdate.getId(), userToUpdate);
        return userToUpdate;
    }

    @Override
    public void deleteUser(Long userId) {
        checkUserById(userId);
        users.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {
        checkUserById(userId);
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    public static void checkUserById(Long userId) {
        if (!users.containsKey(userId))
            throw new NotFoundException(useType(USER));
    }

    private void checkEmail(String email) {
        for (User user : users.values()) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                throw new DuplicateException("Такой email уже зарегистрирован.");
            }
        }
    }
}
