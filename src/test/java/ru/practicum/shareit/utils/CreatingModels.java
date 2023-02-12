package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CreatingModels {
    public User createDefaultUser() {
        return User.builder()
                .id(1L)
                .name("name")
                .email("mail@mail.ru")
                .build();
    }

    public Item createDefaultItem() {
        User owner = createDefaultUser();
        return Item.builder()
                .id(1L)
                .owner(owner)
                .available(true)
                .description("Отличная вещь")
                .name("Вещь")
                .request(null)
                .build();
    }

    public Comment createDefaultComment() {
        User user = createDefaultUser();
        Item item = createDefaultItem();
        return Comment.builder()
                .id(1L)
                .authorName(user.getName())
                .text("Отличная вещь и правда отличная")
                .created(LocalDateTime.now())
                .item(item)
                .build();
    }

    public Booking createDefaultBooking() {
        User user = createDefaultUser();
        Item item = createDefaultItem();
        return Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(user)
                .start(LocalDateTime.now().minusHours(2L))
                .end(LocalDateTime.now().plusDays(5L))
                .build();
    }

    public ItemRequest createDefaultItemRequest() {
        User author = createDefaultUser();
        return ItemRequest.builder()
                .id(1L)
                .items(new ArrayList<>())
                .author(author)
                .description("Нужна отличная вещь")
                .created(LocalDateTime.of(2023, 1, 25, 15, 0))
                .build();
    }
}