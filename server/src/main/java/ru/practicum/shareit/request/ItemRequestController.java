package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    /**
     * Создание нового запроса на вещь.
     *
     * @param itemRequest - объект запроса (должен содержать текст)
     * @param authorId    - id автора запроса
     * @return - созданный запрос.
     */

    @PostMapping
    public ItemRequestResponse createRequest(@RequestBody ItemRequest itemRequest,
                                             @RequestHeader(IDENTIFICATION_HEADER) Long authorId) {
        return itemRequestService.createRequest(itemRequest, authorId);
    }

    /**
     * Получение списка своих запросов.
     *
     * @param authorId - id автора запроса.
     * @return - список своих запросов.
     */

    @GetMapping
    public List<ItemRequestResponse> getAllRequestsByAuthorId(
            @RequestHeader(IDENTIFICATION_HEADER) Long authorId) {
        return itemRequestService.getAllRequestsByAuthorId(authorId);
    }

    /**
     * Получение списка запросов других пользователей.
     *
     * @param userId - id пользователя
     * @param index  - индекс первого элемента для пагинации
     * @param size   - количество элементов отображения при пагинации
     * @return - список запросов пользователей.
     */

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequests(
            @RequestHeader(IDENTIFICATION_HEADER) Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer index,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return itemRequestService.getAllRequestsExceptAuthor(userId, index, size);
    }

    /**
     * Получение запроса по id.
     *
     * @param requestId - id запроса.
     * @return - запрос с указанным id.
     */

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequestById(@RequestHeader(IDENTIFICATION_HEADER) Long userId,
                                              @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}