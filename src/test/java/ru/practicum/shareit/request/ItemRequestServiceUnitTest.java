package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PreparingForUnitTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.exceptions.notfound.ErrorType.REQUEST;
import static ru.practicum.shareit.exceptions.notfound.ErrorType.useType;

public class ItemRequestServiceUnitTest extends PreparingForUnitTest {
    @Test
    void getAllRequestsByAuthorIdTest() {
        ItemRequest itemRequest = createDefaultItemRequest();
        User author = itemRequest.getAuthor();
        long authorId = author.getId();

        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));

        when(requestRepository.findAllByAuthor_IdOrderByCreated(anyLong()))
                .thenReturn(itemRequests);

        List<ItemRequestResponse> requestResponsesForTest =
                ItemRequestMapper.itemRequestToItemRequestResponse(itemRequests);

        List<ItemRequestResponse> requestResponses = requestService.getAllRequestsByAuthorId(authorId);

        assertEquals(requestResponsesForTest, requestResponses);
    }

    @Test
    void getAllRequestsExceptAuthorTest() {
        ItemRequest itemRequest = createDefaultItemRequest();
        User author = itemRequest.getAuthor();
        long authorId = author.getId();

        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));

        Page<ItemRequest> requestPage = new PageImpl<>(itemRequests);

        when(requestRepository.findAllByIdNotOrderByCreated(anyLong(), any()))
                .thenReturn(requestPage);

        List<ItemRequestResponse> requestResponsesForTest =
                ItemRequestMapper.itemRequestToItemRequestResponse(itemRequests);

        List<ItemRequestResponse> requestResponses =
                requestService.getAllRequestsExceptAuthor(authorId, 0, Integer.MAX_VALUE);

        assertEquals(requestResponsesForTest, requestResponses);
    }

    @Test
    void getRequestByIdTest() {
        ItemRequest itemRequest = createDefaultItemRequest();
        User author = itemRequest.getAuthor();
        long authorId = author.getId();
        long requestId = itemRequest.getId();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestResponse requestResponse = requestService.getRequestById(authorId, requestId);
        ItemRequest itemRequestForTest = ItemRequest.builder()
                .id(requestResponse.getId())
                .author(author)
                .created(requestResponse.getCreated())
                .description(requestResponse.getDescription())
                .items(new ArrayList<>())
                .build();

        assertEquals(itemRequest, itemRequestForTest);
    }

    @Test
    void getRequestByWrongIdTest() {
//     При запросе несуществующего запроса, выбросит ошибку.
        ItemRequest itemRequest = createDefaultItemRequest();
        User author = itemRequest.getAuthor();
        long authorId = author.getId();
        long requestId = itemRequest.getId();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(authorId, requestId));

        String errorMessage = useType(REQUEST);
        assertEquals(errorMessage, exception.getMessage());
    }
}
