package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
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
        Long ownerId = item.getOwner().getId();
        ItemRequest itemRequest = createDefaultItemRequest();

        item.setRequest(itemRequest);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemResponse itemResponse = itemService.updateItem(item, ownerId, item.getId());
        Item itemForTest = item;
        itemForTest.setId(itemResponse.getId());

        assertEquals(item, itemForTest);
    }

    @Test
    void updateItemExceptionTest() {
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
    void getItemByIdForOwnerTest() {
        // Если запрос выполняет владелец вещи, должно произойти обращение к БД за отзывами на неё,
        // а также за датами начала и конца бронирования.
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
    void getItemByIdForNotOwnerTest() {
        // Если запрос выполняет не владелец вещи, должно произойти обращение к БД только за комментариями.

        User owner = item.getOwner();
        ItemRequest request = item.getRequest();
        long itemId = item.getId();
        long userId = 5L;

        Comment comment = createDefaultComment();
        List<Comment> comments = List.of(comment);
        List<CommentResponse> commentResponses = ItemMapper.commentsToCommentsResponse(comments);


        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        ItemResponse itemResponse = itemService.getItemById(itemId, userId);
        itemResponse.setComments(commentResponses);

        Item itemForTest = Item.builder()
                .id(itemResponse.getId())
                .name(itemResponse.getName())
                .owner(owner)
                .available(itemResponse.getAvailable())
                .description(itemResponse.getDescription())
                .request(request)
                .build();

        verify(commentRepository, times(1))
                .findAllByItem_Id(itemId);
        verify(bookingRepository, never())
                .findBookingsByItem_IdAndStatusOrderByStart(anyLong(), any());
        verify(bookingRepository, never())
                .findBookingsByItem_IdAndStatusOrderByEndDesc(anyLong(), any());

        ItemResponse itemResponseForTest = ItemMapper.itemToItemResponseWithComments(item, commentResponses);
        assertEquals(itemResponseForTest, itemResponse);
        assertEquals(item, itemForTest);
    }

    @Test
    void getAllItemsByUserIdOwnerTest() {
        //  Если запрос выполняет владелец вещи, должно произойти обращение к БД только за комментариями.

        Item item1 = createDefaultItem();
        item1.setId(2L);
        Item item2 = createDefaultItem();
        item2.setId(3L);
        long userId = 5L;

        Comment comment = createDefaultComment();
        CommentResponse commentResponse = ItemMapper.commentToCommentResponse(comment);
        List<CommentResponse> commentResponses = List.of(commentResponse);

        List<Item> items = List.of(item, item1, item2);
        Page<Item> itemPage = new PageImpl<>(items);
        when(itemRepository.findAllByOwner_Id(anyLong(), any()))
                .thenReturn(itemPage);
        List<ItemResponse> itemResponses = itemService.getAllItemsByUserId(userId, 0, null);
        ItemResponse itemResponse1 = itemResponses.get(0);
        itemResponse1.setComments(commentResponses);

        int amountOfRequests = items.size();

        verify(commentRepository, times(amountOfRequests))
                .findAllByItem_Id(anyLong());
        verify(bookingRepository, never())
                .findBookingsByItem_IdAndStatusOrderByStart(anyLong(), any());
        verify(bookingRepository, never())
                .findBookingsByItem_IdAndStatusOrderByEndDesc(anyLong(), any());

        ItemResponse itemResponseForTest = ItemMapper.itemToItemResponse(item);
        itemResponseForTest.setComments(commentResponses);

        assertEquals(itemResponseForTest, itemResponse1);
    }

    @Test
    void getAllItemsByUserIdNotOwnerTest() {
        //  Если запрос выполняет не владелец вещи, должно произойти обращение к БД за отзывами на неё,
        //  а также за датами начала и конца бронирования.
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
        User owner = item.getOwner();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        PageRequest request = PageRequest.of(0, Integer.MAX_VALUE);
        String text = "Вещь";
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> itemPage = new PageImpl<>(items, request, Integer.MAX_VALUE);

        when(itemRepository.findAllByPartOfName(text, request))
                .thenReturn(itemPage);

        List<ItemResponse> itemResponses = itemService.searchByName(text, owner.getId(), 0, Integer.MAX_VALUE);
        ItemResponse itemResponse = ItemMapper.itemToItemResponse(item);

        assertEquals(1, itemResponses.size());
        assertEquals(itemResponse, itemResponses.get(0));
    }

    @Test
    void searchByNameExceptionTest() {
        // Если передать в метод пустой текст, вернётся пустой список предметов.
        List<ItemResponse> items = itemService.searchByName("", 1L, 0, null);

        assertEquals(0, items.size());
    }

    @Test
    void createCommentTest() {
        Comment comment = createDefaultComment();
        long itemId = comment.getItem().getId();
        long userId = 2L;

        List<Booking> bookings = new ArrayList<>();
        Booking booking = createDefaultBooking();
        bookings.add(booking);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsByBooker_IdAndStartBefore(anyLong(), any()))
                .thenReturn(bookings);
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentResponse commentResponse = itemService.createComment(comment, itemId, userId);
        Comment commentForTest = Comment.builder()
                .id(commentResponse.getId())
                .item(item)
                .text(commentResponse.getText())
                .created(commentResponse.getCreated())
                .authorName(commentResponse.getAuthorName())
                .build();

        assertEquals(commentForTest, comment);
    }

    @Test
    void createCommentExceptionTest() {
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