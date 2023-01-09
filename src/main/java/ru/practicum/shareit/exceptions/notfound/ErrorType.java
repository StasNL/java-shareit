package ru.practicum.shareit.exceptions.notfound;

public enum ErrorType {
    USER, ITEM;

    public static String useType(ErrorType type) {
        if (type == USER)
            return NotFoundException.errorUserMessage;
        else
            return NotFoundException.errorItemMessage;
    }
}
