package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommonService {
    protected final UserRepository userRepository;
    protected final ItemRepository itemRepository;
    protected final ItemRequestRepository requestRepository;
    protected final BookingRepository bookingRepository;
    protected final CommentRepository commentRepository;

    public User getUserWithCheck(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(useType(USER)));
    }

    protected Item getItemWithCheck(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(useType(ITEM)));
    }

    protected ItemRequest getItemRequestWithCheck(Long itemRequestId) {
        return requestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(useType(REQUEST)));
    }

    protected List<Integer> makePaginationParams (Integer index, Integer size) {
        Integer page = index;
        if (size == null)
            size = Integer.MAX_VALUE;
        else page = index / size;
        return List.of(page, size);
    }

    protected Booking getBookingWithCheck(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(useType(BOOKING)));
    }
}
