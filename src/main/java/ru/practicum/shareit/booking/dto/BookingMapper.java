package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static Booking mapToBooking(BookingRequest bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingResponse bookingToBookingResponse(Booking booking) {
        Item item = booking.getItem();
        ItemResponse itemResponse = ItemMapper.itemToItemResponse(item);
        UserResponse booker = UserMapper.userToUserResponse(booking.getBooker());

        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(itemResponse)
                .booker(booker)
                .build();
    }
}
