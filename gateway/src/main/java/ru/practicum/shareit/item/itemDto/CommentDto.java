package ru.practicum.shareit.item.itemDto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotNull
    @NotEmpty
    private String text;
}