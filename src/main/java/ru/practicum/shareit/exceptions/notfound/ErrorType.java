package ru.practicum.shareit.exceptions.notfound;

public enum ErrorType {
    USER, ITEM, OWNER, TEXT, BOOKING, REQUEST;

    public static String useType(ErrorType type) {
        String errorMassage;
        switch (type) {
            case ITEM:
                errorMassage = NotFoundException.errorItemMessage;
                break;
            case USER:
                errorMassage = NotFoundException.errorUserMessage;
                break;
            case OWNER:
                errorMassage = NotFoundException.errorOwnerMessage;
                break;
            case BOOKING:
                errorMassage = NotFoundException.errorBookingMessage;
                break;
            case REQUEST:
                errorMassage = NotFoundException.errorRequestMessage;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return errorMassage;
    }
}