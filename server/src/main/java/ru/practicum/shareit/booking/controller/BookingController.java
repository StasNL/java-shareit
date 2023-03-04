package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest booking,
                                         @RequestHeader(IDENTIFICATION_HEADER) Long userId) {
        return bookingService.createNewBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestParam Boolean approved,
                                               @RequestHeader(IDENTIFICATION_HEADER) Long userId,
                                               @PathVariable Long bookingId) {
        return bookingService.changeBookingStatus(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId,
                                      @RequestHeader(IDENTIFICATION_HEADER) Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByBooker(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(IDENTIFICATION_HEADER) Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer index,
            @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getBookingsByBooker(state, userId, index, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(IDENTIFICATION_HEADER) Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer index,
            @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getBookingsByOwner(state, userId, index, size);
    }
}