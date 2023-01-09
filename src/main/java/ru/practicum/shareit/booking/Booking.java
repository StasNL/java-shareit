package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private Long itemId;
    private Long customerId;
    private LocalDate bookTime;
    private boolean confirmation;
    private String review;
    //  Оценка услуги при написании отзыва.
    private Integer rating;
}
