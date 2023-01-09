package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserResponse {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
    private int likes;
}
