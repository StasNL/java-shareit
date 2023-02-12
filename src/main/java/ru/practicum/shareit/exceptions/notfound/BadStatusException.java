package ru.practicum.shareit.exceptions.notfound;

public class BadStatusException extends IllegalArgumentException {
    public BadStatusException(String message) {
        super(message);
    }
}