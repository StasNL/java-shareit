package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.CreatingModels;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest extends CreatingModels {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    void getBookingsByBookerTest() {
//     Создание владельца вещи.
        User owner = createDefaultUser();
        owner.setId(null);
        UserResponse userResponse1 = userService.createUser(owner);
        long ownerId = userResponse1.getId();
        owner.setId(ownerId);

//    Создание автора бронирования.
        User booker = createDefaultUser();
        booker.setId(null);
        booker.setEmail("booker@mail.ru");
        UserResponse userResponse2 = userService.createUser(booker);
        long bookerId = userResponse2.getId();
        booker.setId(bookerId);

//     Создание вещи.
        Item item = createDefaultItem();
        item.setId(null);
        item.setOwner(owner);
        ItemDtoForCreate itemDto = ItemMapper.itemToNewItemDto(item);
        ItemResponse itemResponse = itemService.createItem(itemDto, ownerId);
        long itemId = itemResponse.getId();
        item.setId(itemId);

//     Создание бронирования.
        Booking booking = createDefaultBooking();
        booking.setId(null);
        booking.setBooker(booker);
        booking.setItem(item);
        BookingRequest bookingRequest = BookingMapper.bookingToBookingRequest(booking);
        BookingResponse bookingResponse = bookingService.createNewBooking(bookingRequest, bookerId);
        long bookingId = bookingResponse.getId();
        booking.setId(bookingId);

//     Запрос в БД.
        TypedQuery<Booking> query =
                em.createQuery("select b from Booking b where b.booker.id = ?1", Booking.class);
        query.setParameter(1, bookerId);

        Booking bookingFromDb = query.getSingleResult();

        assertEquals(booking, bookingFromDb);
    }
}