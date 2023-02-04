package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String identificationHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody @Valid BookingRequest booking,
                                         @RequestHeader(identificationHeader) @NotNull Long userId) {
        return bookingService.createNewBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestParam @NotNull Boolean approved,
                                               @RequestHeader(identificationHeader) @NotNull Long userId,
                                               @PathVariable @NotNull Long bookingId) {
        return bookingService.changeBookingStatus(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable @NotNull Long bookingId,
                                      @RequestHeader(identificationHeader) @NotNull Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByBooker(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(identificationHeader) @NotNull Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false) @Positive Integer size) {
        return bookingService.getBookingsByBooker(state, userId, index, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(identificationHeader) @NotNull Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false) @Positive Integer size) {
        return bookingService.getBookingsByOwner(state, userId, index, size);
    }
}
