package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createNewBooking(BookingRequest bookingDto, Long userId);

    BookingResponse changeBookingStatus(Boolean approved, Long bookingId, Long userId);

    BookingResponse getBooking(Long bookingId, Long userId);

    List<BookingResponse> getBookingsByBooker(String state, Long userId, Integer index, Integer size);

    List<BookingResponse> getBookingsByOwner(String state, Long userId, Integer index, Integer size);
}
