package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CommonService;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemRequestServiceImpl extends CommonService implements ItemRequestService {

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemRequestRepository requestRepository,
                                  BookingRepository bookingRepository,
                                  CommentRepository commentRepository) {
        super(userRepository, itemRepository, requestRepository, bookingRepository, commentRepository);
    }

    @Transactional
    @Override
    public ItemRequestResponse createRequest(ItemRequest itemRequest, Long authorId) {
        User author = getUserWithCheck(authorId);
        itemRequest.setAuthor(author);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = requestRepository.save(itemRequest);
        log.info("Запрос успешно создан с id = " + itemRequest.getId());
        return ItemRequestMapper.itemRequestToItemRequestResponse(itemRequest);
    }

    @Override
    public List<ItemRequestResponse> getAllRequestsByAuthorId(Long authorId) {
        getUserWithCheck(authorId);
        List<ItemRequest> itemRequests = requestRepository.findAllByAuthor_IdOrderByCreated(authorId);
        log.info("Список запросов пользователя с id = " + authorId + " успешно получен.");
        return ItemRequestMapper.itemRequestToItemRequestResponse(itemRequests);
    }

    @Override
    public List<ItemRequestResponse> getAllRequestsExceptAuthor(Long userId, Integer index, Integer size) {
        getUserWithCheck(userId);
        List<Integer> params = makePaginationParams(index, size);

        Page<ItemRequest> itemRequests = requestRepository.findAllByIdNotOrderByCreated(userId,
                PageRequest.of(params.get(0), params.get(1)));
        List<ItemRequest> irr = itemRequests.toList();

        return ItemRequestMapper.itemRequestToItemRequestResponse(irr);
    }

    @Override
    public ItemRequestResponse getRequestById(Long userId, Long requestId) {
        getUserWithCheck(userId);
        ItemRequest itemRequest = getItemRequestWithCheck(requestId);
        log.info("Запрос с id = " + requestId + " успешно найден.");
        return ItemRequestMapper.itemRequestToItemRequestResponse(itemRequest);
    }
}