package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CommonService;
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
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl extends CommonService implements BookingService {
    @Autowired
    public BookingServiceImpl(UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingRepository bookingRepository,
                              ItemRequestRepository requestRepository,
                              CommentRepository commentRepository) {
        super(userRepository, itemRepository, requestRepository, bookingRepository, commentRepository);
    }

    @Transactional
    @Override
    public BookingResponse createNewBooking(BookingRequest bookingDto, Long userId) {
        checkTime(bookingDto);
        User booker = getUserWithCheck(userId);
        Long itemId = bookingDto.getItemId();
        Item item = getItemWithCheck(itemId);

        if (!item.getAvailable())
            throw new BadRequestException("Предмет не доступен для аренды.");

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
        Booking booking = getBookingWithCheck(bookingId);
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
        Booking booking = getBookingWithCheck(bookingId);
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();
        if (userId != bookerId && userId != ownerId)
            throw new NotFoundException("Просмотр запроса на бронирование доступен только для автора " +
                    "бронирования, либо для владельца предмета");
        return BookingMapper.bookingToBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByBooker(String state, Long userId, Integer index, Integer size) {
        getUserWithCheck(userId);
        StateParam stateParam = checkState(state);

        LocalDateTime dateTime = LocalDateTime.now();
        List<Integer> params = makePaginationParams(index, size);
        index = params.get(0);
        size = params.get(1);
        Page<Booking> bookings;
        switch (stateParam) {
            case ALL:
                bookings = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(userId,
                        PageRequest.of(index, size));
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsWithDateBetweenStartAndEnd(userId,
                        dateTime,
                        PageRequest.of(index, size));
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(userId,
                        dateTime,
                        PageRequest.of(index, size));
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(userId,
                        dateTime,
                        PageRequest.of(index, size));
                break;
            default:
                String statusString = stateParam.toString();
                Status status = Status.valueOf(statusString);
                bookings = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(userId,
                        status,
                        PageRequest.of(index, size));
        }
        return BookingMapper.bookingToBookingResponse(bookings.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByOwner(String state, Long userId, Integer index, Integer size) {
        getUserWithCheck(userId);
        StateParam stateParam = checkState(state);

        List<Integer> params = makePaginationParams(index, size);
        index = params.get(0);
        size = params.get(1);

        LocalDateTime dateTime = LocalDateTime.now();
        Page<Booking> bookings;
        switch (stateParam) {
            case ALL:
                bookings = bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(userId,
                        PageRequest.of(index, size));
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItem_Owner_IdWithDateBetweenStartAndEnd(userId,
                        dateTime,
                        PageRequest.of(index, size));
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartAfterOrderByStartDesc(userId,
                        dateTime,
                        PageRequest.of(index, size));
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId,
                        dateTime,
                        PageRequest.of(index, size));
                break;
            default:
                String statusString = stateParam.toString();
                Status status = Status.valueOf(statusString);
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(userId,
                        status,
                        PageRequest.of(index, size));
        }
        return BookingMapper.bookingToBookingResponse(bookings.toList());
    }

    private void checkTime(BookingRequest booking) {
        if (booking.getStart()
                .isAfter(booking.getEnd()))
            throw new BadRequestException("Время начала и конца бронирования перепутаны местами.");
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
