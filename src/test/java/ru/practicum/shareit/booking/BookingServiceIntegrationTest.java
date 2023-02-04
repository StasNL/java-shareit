package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
        userService.createUser(owner);
//    Создание автора бронирования.
        User booker = createDefaultUser();
        booker.setId(2L);
        booker.setEmail("booker@mail.ru");
        userService.createUser(booker);

//     Создание вещи.
        Item item = createDefaultItem();
        ItemDtoForCreate itemDto = ItemMapper.itemToNewItemDto(item);
        itemService.createItem(itemDto, owner.getId());

//     Создание бронирования.
        Booking booking = createDefaultBooking();
        booking.setBooker(booker);
        BookingRequest bookingRequest = BookingMapper.bookingToBookingRequest(booking);
        bookingService.createNewBooking(bookingRequest, booker.getId());

//     Запрос в БД.
        TypedQuery<Booking> query =
                em.createQuery("select b from Booking b where b.booker.id = ?1", Booking.class);
        query.setParameter(1, booker.getId());

        Booking bookingFromDb = query.getSingleResult();

        assertEquals(booking, bookingFromDb);
    }
}