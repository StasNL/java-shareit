package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID_FROM_HEADER;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid BookingRequest booking,
                                         @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId) {
        return bookingService.createNewBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestParam @NotNull Boolean approved,
                                               @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId,
                                               @PathVariable @NotNull Long bookingId) {
        return bookingService.changeBookingStatus(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable @NotNull Long bookingId,
                                      @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByBooker(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId) {
        return bookingService.getBookingsByBooker(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId) {
        return bookingService.getBookingsByOwner(state, userId);
    }
}
