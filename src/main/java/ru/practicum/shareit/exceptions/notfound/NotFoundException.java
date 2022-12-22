package ru.practicum.shareit.exceptions.notfound;

public class NotFoundException extends RuntimeException{
protected static final String errorUserMessage = "Данный пользователь не зарегистрирован";
protected static final String errorItemMessage = "Данный предмет не найден";

    public NotFoundException(String message) {
        super(message);
    }
}
