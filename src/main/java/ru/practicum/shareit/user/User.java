package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
//  При написании отзыва можно оценить вещь.
//  В данной графе будет записываться средний рейтинг по всем отзывам.
    private Float avgRating;
}
