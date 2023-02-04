package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.utils.PreparingForUnitTest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceUnitTest extends PreparingForUnitTest {

    @Test
    void createNewBookingWrongStartAndEndTest() {
        // Если происходит попытка бронирования c перепутанными местами временем начала и конца бронирования.

        Booking booking = createDefaultBooking();
        booking.setStart(LocalDateTime.now().minusDays(1L));
        booking.setEnd(LocalDateTime.now().minusDays(2L));

        long userId = 2L;

        BookingRequest bookingRequest = BookingMapper.bookingToBookingRequest(booking);

        Throwable exception = assertThrows(BadRequestException.class,
                () -> bookingService.createNewBooking(bookingRequest, userId));

        assertEquals("Время начала и конца бронирования перепутаны местами.", exception.getMessage());
    }
    @Test
    void createNewBookingNotAvailableTest() {
        // Если происходит попытка бронирования вещи, бронирование которой не доступно, выбрасывается ошибка.
        User user = createDefaultUser();
        Item item = createDefaultItem();
        item.setAvailable(false);

        Booking booking = createDefaultBooking();
        booking.setItem(item);

        long userId = 2L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingRequest bookingRequest = BookingMapper.bookingToBookingRequest(booking);

        Throwable exception = assertThrows(BadRequestException.class,
                () -> bookingService.createNewBooking(bookingRequest, userId));

        assertEquals("Предмет не доступен для аренды.", exception.getMessage());
    }

    @Test
    void createNewBookingOwnerTest() {
        // Собственник не может забронировать собственную вещь.
        User user = createDefaultUser();
        Item item = createDefaultItem();

        Booking booking = createDefaultBooking();
        booking.setItem(item);

        long userId = item.getOwner().getId();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingRequest bookingRequest = BookingMapper.bookingToBookingRequest(booking);

        Throwable exception = assertThrows(NotFoundException.class,
                () -> bookingService.createNewBooking(bookingRequest, userId));

        assertEquals("Собственник не может арендовать собственную вещь.", exception.getMessage());
    }

    @Test
    void changeBookingStatusApprovedTest() {
        Booking booking = createDefaultBooking();

        long bookingId = booking.getId();
        long userId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponse bookingResponse = bookingService.changeBookingStatus(true, bookingId, userId);
        booking.setStatus(Status.APPROVED);

        BookingResponse bookingResponseForTest = BookingMapper.bookingToBookingResponse(booking);

        assertEquals(bookingResponseForTest, bookingResponse);
    }

    @Test
    void changeBookingStatusRejectedTest() {
        Booking booking = createDefaultBooking();

        long bookingId = booking.getId();
        long userId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponse bookingResponse = bookingService.changeBookingStatus(false, bookingId, userId);
        booking.setStatus(Status.REJECTED);

        BookingResponse bookingResponseForTest = BookingMapper.bookingToBookingResponse(booking);

        assertEquals(bookingResponseForTest, bookingResponse);
    }

    @Test
    void changeBookingStatusNotOwnerTest() {
        // Если статус бронирования пытается сменить не собственник, выбрасывается ошибка.
        Booking booking = createDefaultBooking();
        long bookingId = booking.getId();
        long userId = 2L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> bookingService.changeBookingStatus(true, bookingId, userId));

        assertEquals("Изменять статус бронирования может только собственник вещи", exception.getMessage());
    }

    @Test
    void changeBookingStatusDoubleApprovedTest() {
        // Если делается попытка одобрить уже одобренное бронирование, выбрасывается ошибка.
        Booking booking = createDefaultBooking();
        booking.setStatus(Status.APPROVED);

        long bookingId = booking.getId();
        long userId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(BadRequestException.class,
                () -> bookingService.changeBookingStatus(true, bookingId, userId));

        assertEquals("Бронирование уже одобрено", exception.getMessage());
    }

    @Test
    void changeBookingStatusDoubleRejectedTest() {
        // Если делается попытка отклонить уже отклонённое бронирование, выбрасывается ошибка.
        Booking booking = createDefaultBooking();
        booking.setStatus(Status.REJECTED);

        long bookingId = booking.getId();
        long userId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(BadRequestException.class,
                () -> bookingService.changeBookingStatus(false, bookingId, userId));

        assertEquals("Бронирование уже было отклонено", exception.getMessage());
    }

    @Test
    void getBookingTest() {
        Booking booking = createDefaultBooking();
        long bookingId = booking.getId();
        long userId = booking.getBooker().getId();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingResponse bookingResponse = bookingService.getBooking(bookingId, userId);
        BookingResponse bookingResponseForTest = BookingMapper.bookingToBookingResponse(booking);

        assertEquals(bookingResponseForTest, bookingResponse);
    }

    @Test
    void getBookingNotOwnerNotBookerTest() {
        // Если бронирование запрашивает не владелец вещи и не автор бронирования, выбрасывает ошибку.
        Booking booking = createDefaultBooking();
        long bookingId = booking.getId();
        long userId = 2L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(bookingId, userId));

        assertEquals("Просмотр запроса на бронирование доступен только для автора " +
                "бронирования, либо для владельца предмета", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerWithStateAllTest() {
        // При значении параметра state = ALL, должен совершаться запрос всех бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingPage);

        String state = "ALL";
        long userId = user.getId();

        bookingService.getBookingsByOwner(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByItem_Owner_IdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwnerWithStateCurrentTest() {
        // При значении параметра state = CURRENT, должен совершаться запрос текущих бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByItem_Owner_IdWithDateBetweenStartAndEnd(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "CURRENT";
        long userId = user.getId();

        bookingService.getBookingsByOwner(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByItem_Owner_IdWithDateBetweenStartAndEnd(anyLong(), any(), any());
    }

    @Test
    void getBookingsByOwnerWithStateFutureTest() {
        // При значении параметра state = FUTURE, должен совершаться запрос будущих бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByItem_Owner_IdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "FUTURE";
        long userId = user.getId();

        bookingService.getBookingsByOwner(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByItem_Owner_IdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByOwnerWithStatePastTest() {
        // При значении параметра state = PAST, должен совершаться запрос прошлых бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByItem_Owner_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "PAST";
        long userId = user.getId();

        bookingService.getBookingsByOwner(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByItem_Owner_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByOwnerWithStateWaitingTest() {
        // При значении параметра state = WAITING, должен совершаться запрос ожидающих одобрения
        // бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "WAITING";
        long userId = user.getId();

        bookingService.getBookingsByOwner(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByOwnerWithStateRejectedTest() {
        // При значении параметра state = REJECTED, должен совершаться запрос отклонённых
        // бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "REJECTED";
        long userId = user.getId();

        bookingService.getBookingsByOwner(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBookerWithStateAllTest() {
        // При значении параметра state = ALL, должен совершаться запрос всех бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByBooker_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingPage);

        String state = "ALL";
        long userId = user.getId();

        bookingService.getBookingsByBooker(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByBookerWithStateCurrentTest() {
        // При значении параметра state = CURRENT, должен совершаться запрос текущих бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsWithDateBetweenStartAndEnd(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "CURRENT";
        long userId = user.getId();

        bookingService.getBookingsByBooker(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsWithDateBetweenStartAndEnd(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBookerWithStateFutureTest() {
        // При значении параметра state = FUTURE, должен совершаться запрос будущих бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "FUTURE";
        long userId = user.getId();

        bookingService.getBookingsByBooker(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBookerWithStatePastTest() {
        // При значении параметра state = PAST, должен совершаться запрос прошлых бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "PAST";
        long userId = user.getId();

        bookingService.getBookingsByBooker(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBookerWithStateWaitingTest() {
        // При значении параметра state = WAITING, должен совершаться запрос ожидающих одобрения
        // бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "WAITING";
        long userId = user.getId();

        bookingService.getBookingsByBooker(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBookerWithStateRejectedTest() {
        // При значении параметра state = REJECTED, должен совершаться запрос отклонённых
        // бронирований пользователя.
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        String state = "REJECTED";
        long userId = user.getId();

        bookingService.getBookingsByBooker(state, userId, 0, null);

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }
}