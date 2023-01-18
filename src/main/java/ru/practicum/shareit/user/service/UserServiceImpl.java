package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional
    public UserResponse createUser(User user) {
        if (user.getEmail() == null)
            throw new BadRequestException("Графа email пуста");
        user = repository.save(user);
        log.info("Пользователь успешно создан.");
        return UserMapper.userToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(User userToUpdate, long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException(useType(USER)));

        if (userToUpdate.getName() != null)
            user.setName(userToUpdate.getName());

        if (userToUpdate.getEmail() != null)
            user.setEmail(userToUpdate.getEmail());

        log.info("Данные пользователя с id = " + userId + " отредактированы.");
        return UserMapper.userToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        repository.findById(userId).orElseThrow(() -> new NotFoundException(useType(USER)));
        repository.deleteById(userId);
        log.info("Пользователь c id = " + userId + " удалён.");
    }

    public UserResponse getUserById(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException(useType(USER)));
        log.info("Пользователь с id = " + userId + " найден.");
        return UserMapper.userToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = repository.findAll();
        log.info("Список пользователей получен.");
        return UserMapper.userToUserResponse(users);
    }
}
