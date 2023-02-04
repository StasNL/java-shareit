package ru.practicum.shareit.exceptions.notfound;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorMessage {
    private final String error;
}