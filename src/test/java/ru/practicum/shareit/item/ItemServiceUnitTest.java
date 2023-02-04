package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.utils.PreparingForUnitTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class ItemServiceUnitTest extends PreparingForUnitTest {
    Item item;

    @BeforeEach
    void createItem() {
        item = createDefaultItem();
    }

    @BeforeEach
    void mockCheckUser() {
        User user = createDefaultUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void createItemTest() {
        // Если поле request не равно нулю, должен быть выполнен поиск данного запроса в базе данных.
        Long ownerId = item.getOwner().getId();
        ItemRequest itemRequest = createDefaultItemRequest();

        item.setRequest(itemRequest);

        when(itemRepository.save(any()))
                .thenReturn(item);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemDtoForCreate itemDto = ItemMapper.itemToNewItemDto(item);

        itemService.createItem(itemDto, ownerId);

        verify(requestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void updateItemTest() {
        //Если пользователь не является владельцем, должна выпасть ошибка NotFoundException.
        long userId = 2L;
        long itemId = item.getId();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item, userId, itemId));

        assertEquals("Вещь с указанным id у данного собственника не найдена.", exception.getMessage());
    }

    @Test
    void getItemByIdTest() {
        // Если запрос выполняет владелец вещи, должно произойти обращение к БД за отзывами на неё,
        // а также за датами начала и конца бронинования.
        long itemId = item.getId();
        long userId = item.getOwner().getId();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        itemService.getItemById(itemId, userId);

        verify(commentRepository, times(1))
                .findAllByItem_Id(itemId);
        verify(bookingRepository, times(1))
                .findBookingsByItem_IdAndStatusOrderByStart(anyLong(), any());
        verify(bookingRepository, times(1))
                .findBookingsByItem_IdAndStatusOrderByEndDesc(anyLong(), any());
    }

    @Test
    void getAllItemsByUserIdTest() {
        //  Если запрос выполняет владелец вещи, должно произойти обращение к БД за отзывами на неё,
        //  а также за датами начала и конца бронинования.
        //  Количество запросов совпадает с количеством предметов у пользователя.
        Item item1 = createDefaultItem();
        item1.setId(2L);
        Item item2 = createDefaultItem();
        item2.setId(3L);
        long userId = item.getOwner().getId();
        List<Item> items = List.of(item, item1, item2);
        Page<Item> itemPage = new PageImpl<>(items);

        when(itemRepository.findAllByOwner_Id(anyLong(), any()))
                .thenReturn(itemPage);
        itemService.getAllItemsByUserId(userId, 0, null);

        int amountOfRequests = items.size();

        verify(commentRepository, times(amountOfRequests))
                .findAllByItem_Id(anyLong());
        verify(bookingRepository, times(amountOfRequests))
                .findBookingsByItem_IdAndStatusOrderByStart(anyLong(), any());
        verify(bookingRepository, times(amountOfRequests))
                .findBookingsByItem_IdAndStatusOrderByEndDesc(anyLong(), any());
    }

    @Test
    void searchByNameTest() {
        // Если передать в метод пустой текст, вернётся пустой список предметов.
        List<ItemResponse> items = itemService.searchByName("", 1L, 0, null);

        assertEquals(0, items.size());
    }

    @Test
    void createCommentTest() {
        // Если пользователь не пользовался предметом (то есть в базе не найдётся запросов для данного пользователя
        // со временем начала бронирования до настоящего времени), выскакивает ошибка.
        Comment comment = createDefaultComment();
        long itemId = comment.getItem().getId();
        long userId = 2L;

        List<Booking> bookings = new ArrayList<>();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsByBooker_IdAndStartBefore(anyLong(), any()))
                .thenReturn(bookings);

        Throwable exception = assertThrows(BadRequestException.class,
                () -> itemService.createComment(comment, itemId, userId));
        assertEquals("Данный пользователь не пользовался предметом.", exception.getMessage());
    }
}