package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl extends CommonService implements UserService {
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ItemRepository itemRepository,
                           ItemRequestRepository requestRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        super(userRepository, itemRepository, requestRepository, bookingRepository, commentRepository);
    }

    @Transactional
    public UserResponse createUser(User user) {
        if (user.getEmail() == null)
            throw new BadRequestException("Графа email пуста");
        user = userRepository.save(user);
        log.info("Пользователь успешно создан.");
        return UserMapper.userToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(User userToUpdate, Long userId) {
        User user = getUserWithCheck(userId);

        if (userToUpdate.getName() != null)
            user.setName(userToUpdate.getName());

        if (userToUpdate.getEmail() != null)
            user.setEmail(userToUpdate.getEmail());

        user = userRepository.save(user);
        log.info("Данные пользователя с id = " + user.getId() + " отредактированы.");
        return UserMapper.userToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        getUserWithCheck(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь c id = " + userId + " удалён.");
    }

    public UserResponse getUserById(Long userId) {
        User user = getUserWithCheck(userId);
        log.info("Пользователь с id = " + userId + " найден.");
        return UserMapper.userToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Список пользователей получен.");
        return UserMapper.userToUserResponse(users);
    }
}