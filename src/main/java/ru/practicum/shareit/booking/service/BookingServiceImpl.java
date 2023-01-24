package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.StateParam;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.exceptions.notfound.BadStatusException;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingResponse createNewBooking(BookingRequest bookingDto, Long userId) {
        checkTime(bookingDto);
        User booker = checkUser(userId);
        Item item = checkItem(bookingDto);

        Long ownerId = item.getOwner().getId();
        if (ownerId.equals(userId))
            throw new NotFoundException("Собственник не может арендовать собственную вещь.");

        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);
        log.info(item.getName() + " с id = " + item.getId() + " успешно забронирован пользователем с id = " + userId);
        return BookingMapper.bookingToBookingResponse(booking);
    }

    @Transactional
    @Override
    public BookingResponse changeBookingStatus(Boolean approved, Long bookingId, Long userId) {
        Booking booking = checkBooking(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Status status = booking.getStatus();
        if (!userId.equals(ownerId))
            throw new NotFoundException("Изменять статус бронирования может только собственник вещи");
        if (approved) {
            if (!status.equals(Status.APPROVED)) {
                booking.setStatus(Status.APPROVED);
                log.info("Бронирование предмета одобрено собственником");
            } else
                throw new BadRequestException("Бронирование уже одобрено");
        } else {
            if (!status.equals(Status.REJECTED)) {
                booking.setStatus(Status.REJECTED);
                log.info("Бронирование отклонено собственником");
            } else
                throw new BadRequestException("Бронирование уже было отклонено");
        }
        return BookingMapper.bookingToBookingResponse(booking);
    }

    @Override
    public BookingResponse getBooking(Long bookingId, Long userId) {
        Booking booking = checkBooking(bookingId);
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();
        if (userId != bookerId && userId != ownerId)
            throw new NotFoundException("Просмотр запроса на бронирование доступен только для автора " +
                    "бронирования, либо для владельца предмета");
        return BookingMapper.bookingToBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByBooker(String state, Long userId) {
        checkUser(userId);
        StateParam stateParam = checkState(state);

        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings;
        switch (stateParam) {
            case ALL:
                bookings = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsWithDateBetweenStartAndEnd(userId, dateTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(userId, dateTime);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(userId, dateTime);
                break;
            default:
                String statusString = stateParam.toString();
                Status status = Status.valueOf(statusString);
                bookings = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(userId, status);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByOwner(String state, Long userId) {
        checkUser(userId);
        StateParam stateParam = checkState(state);

        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings;
        switch (stateParam) {
            case ALL:
                bookings = bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItem_Owner_IdWithDateBetweenStartAndEnd(userId, dateTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, dateTime);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, dateTime);
                break;
            default:
                String statusString = stateParam.toString();
                Status status = Status.valueOf(statusString);
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(userId, status);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToBookingResponse)
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(useType(USER)));
    }

    private void checkTime(BookingRequest booking) {
        if (booking.getStart()
                .isAfter(booking.getEnd()))
            throw new BadRequestException("Время начала и конца бронирования перепутаны местами.");
    }

    private Item checkItem(BookingRequest booking) {
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException(useType(ITEM)));
        if (!item.getAvailable())
            throw new BadRequestException("Предмет не доступен для аренды.");
        return item;
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(useType(BOOKING)));
    }

    private StateParam checkState(String state) {
        StateParam stateParam;
        try {
            stateParam = StateParam.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadStatusException("Unknown state: " + state);
        }
        return stateParam;
    }
}
