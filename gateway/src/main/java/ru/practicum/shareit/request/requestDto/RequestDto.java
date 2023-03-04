package ru.practicum.shareit.request.requestDto;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RequestDto {
    @NotNull
    private String description;
}