package ru.practicum.shareit.item.itemDto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}