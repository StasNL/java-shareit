package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class ItemUpdate {
    private String name;
    private String description;
    private Boolean available;
}
