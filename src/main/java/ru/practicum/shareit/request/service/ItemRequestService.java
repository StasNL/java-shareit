package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse createRequest(ItemRequest request, Long authorId);

    List<ItemRequestResponse> getAllRequestsByAuthorId(Long authorId);

    List<ItemRequestResponse> getAllRequestsExceptAuthor(Long authorId, Integer index, Integer size);

    ItemRequestResponse getRequestById(Long userId, Long requestId);
}
