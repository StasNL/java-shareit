package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDto booking,
                                                @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId) {
        return bookingClient.createNewBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@RequestParam @NotNull Boolean approved,
                                                      @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
                                                      @PathVariable @NotNull Long bookingId) {
        return bookingClient.changeBookingStatus(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable @NotNull Long bookingId,
                                             @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        Boolean isOwner = false;
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, index, size);
        return bookingClient.getBookings(state, userId, index, size, isOwner);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        Boolean isOwner = true;
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, index, size);
        return bookingClient.getBookings(state, userId, index, size, isOwner);
    }
}