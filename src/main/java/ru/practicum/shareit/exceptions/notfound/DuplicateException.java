package ru.practicum.shareit.exceptions.notfound;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
