package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.CreatingModels;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@Sql("classpath:schemaTest.sql")
public class BookingRepositoryTest extends CreatingModels {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private User booker;
    private Booking booking;

    private final PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);

    @BeforeEach
    void createModelsWithDatesStartPastAndEndFuture() {
        User user = createDefaultUser();
        user.setId(null);
        User owner = userRepository.save(user);
        booker = userRepository.save(user);

        Item item = createDefaultItem();
        item.setId(null);
        item.setOwner(owner);
        item = itemRepository.save(item);

        booking = createDefaultBooking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().plusDays(2L));
        booking = bookingRepository.save(booking);
    }

    @Test
    void findBookingsWithDateBetweenStartAndEndTest() {
        long bookerId = booker.getId();
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings =
                bookingRepository.findBookingsWithDateBetweenStartAndEnd(bookerId, now, pageRequest).toList();
        Booking bookingFromDb = bookings.get(0);

        assertEquals(booking, bookingFromDb);
    }

    @Test
    void findBookingsByItem_Owner_IdWithDateBetweenStartAndEndTest() {
        long ownerId = booking.getItem().getOwner().getId();
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings =
                bookingRepository
                        .findBookingsByItem_Owner_IdWithDateBetweenStartAndEnd(ownerId, now, pageRequest)
                        .toList();
        Booking bookingFromDb = bookings.get(0);

        assertEquals(booking, bookingFromDb);
    }
}