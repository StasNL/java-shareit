package ru.practicum.shareit.exceptions.notfound;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
