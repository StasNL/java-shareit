package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Поиск всех бронирований по id автора бронирования.
     */

    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Long bookerId);

    /**
     * Поиск всех бронирований по id автора бронирования, отвечающих параметру CURRENT.
     * Т.е. тех, стартовое время которых раньше насотящего, а финишное время - позднее.
     */

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and ?2 between b.start and b.end " +
            "order by b.start desc ")
    List<Booking> findBookingsWithDateBetweenStartAndEnd(Long bookerId, LocalDateTime dateTime);

    /**
     * Поиск всех бронирований по id автора бронирования, отвечающих параметру FUTURE.
     * Т.е. тех, стартовое время которых позднее настоящего.
     */

    List<Booking> findBookingsByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    /**
     * Поиск всех бронирований по id автора бронирования, отвечающих параметру PAST.
     * Т.е. тех, финишное время которых ранее настоящего.
     */

    List<Booking> findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(Long bookingId, LocalDateTime dateTime);

    /**
     * Поиск всех бронирований по id автора бронирования, отвечающих параметру WAITING или REJECTED.
     * Т.е. тех, статус бронирования которых ещё не подтверждён владельцем вещи или отклонён им.
     *
     * @param status - WAITING или REJECTED, в зависимости от запроса.
     */

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartDesc(Long bookingId, Status status);

    /**
     * Поиск всех бронирований всех вещей по id владельца.
     */

    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long ownerId);

    /**
     * Поиск всех бронирований всех вещей по id владельца, отвечающих параметру CURRENT.
     * Т.е. тех, стартовое время которых раньше насотящего, а финишное время - позднее.
     */

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and ?2 between b.start and b.end " +
            "order by b.start desc ")
    List<Booking> findBookingsByItem_Owner_IdWithDateBetweenStartAndEnd(Long bookerId, LocalDateTime dateTime);

    /**
     * Поиск всех бронирований всех вещей по id владельца, отвечающих параметру FUTURE.
     * Т.е. тех, стартовое время которых позднее настоящего.
     */

    List<Booking> findBookingsByItem_Owner_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    /**
     * Поиск всех бронирований всех вещей по id владельца, отвечающих параметру PAST.
     * Т.е. тех, финишное время которых ранее настоящего.
     */

    List<Booking> findBookingsByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long bookingId, LocalDateTime dateTime);

    /**
     * Поиск всех бронирований всех вещей по id владельца, отвечающих параметру WAITING или REJECTED.
     * Т.е. тех, статус бронирования которых ещё не подтверждён владельцем вещи или отклонён им.
     *
     * @param status - WAITING или REJECTED, в зависимости от запроса.
     */

    List<Booking> findBookingsByItem_Owner_IdAndStatusOrderByStartDesc(Long bookingId, Status status);

    /**
     * Поиск всех бронирований по id вещи и статусу в порядке убывания даты окончания бронирования.
     */

    List<Booking> findBookingsByItem_IdAndStatusOrderByEndDesc(Long itemId, Status status);

    /**
     * Поиск всех бронирований по id вещи и статусу в порядке возрастания даты начала бронирования.
     */

    List<Booking> findBookingsByItem_IdAndStatusOrderByStart(Long itemId, Status status);

    /**
     * Поиск всех бронирований по id пользователя, время бронирования которой начинается поднее указанной даты
     */

    List<Booking> findBookingsByBooker_IdAndStartBefore(Long userId, LocalDateTime dateTime);
}
