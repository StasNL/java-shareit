package ru.practicum.shareit.exceptions.notfound;

public class NotFoundException extends RuntimeException {
    protected static final String errorUserMessage = "Данный пользователь не зарегистрирован";
    protected static final String errorItemMessage = "Данный предмет не найден";
    protected static final String errorOwnerMessage = "Вещь с указанным id у данного собственника не найдена.";
    protected static final String errorTextMessage = "Данный текст не встречается ни в названии, ни в описании вещи.";
    protected static final String errorBookingMessage = "Бронирование с заданным id не существует.";
    protected static final String errorRequestMessage = "Запрос с заданным id не существует";

    public NotFoundException(String message) {
        super(message);
    }
}
