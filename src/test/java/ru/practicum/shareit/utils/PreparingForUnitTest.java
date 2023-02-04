package ru.practicum.shareit.utils;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PreparingForUnitTest extends CreatingModels {
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ItemRepository itemRepository;
    @Mock
    protected ItemRequestRepository requestRepository;
    @Mock
    protected BookingRepository bookingRepository;
    @Mock
    protected CommentRepository commentRepository;
    @InjectMocks
    protected UserServiceImpl userService;
    @InjectMocks
    protected ItemServiceImpl itemService;
    @InjectMocks
    protected BookingServiceImpl bookingService;
}