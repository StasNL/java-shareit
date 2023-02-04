package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final String identificationHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    /**
     * Создание нового запроса на вещь.
     * @param itemRequest - объект запроса (должен содержать текст)
     * @param authorId - id автора запроса
     * @return - созданный запрос.
     */

    @PostMapping
    public ItemRequestResponse createRequest(@RequestBody @Valid ItemRequest itemRequest,
                                             @RequestHeader(identificationHeader) @NotNull Long authorId) {
        return itemRequestService.createRequest(itemRequest, authorId);
    }

    /**
     * Получение списка своих запросов.
     * @param authorId - id автора запроса.
     * @return - список своих запросов.
     */

    @GetMapping
    public List<ItemRequestResponse> getAllRequestsByAuthorId(
            @RequestHeader(identificationHeader) @NotNull Long authorId) {
        return itemRequestService.getAllRequestsByAuthorId(authorId);
    }

    /**
     * Получение списка запросов других пользователей.
     * @param userId - id пользователя
     * @param index - индекс первого элемента для пагинации
     * @param size - количество элементов отображения при пагинации
     * @return - список запросов пользователей.
     */

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequests(
            @RequestHeader(identificationHeader) @NotNull Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false) @Positive Integer size
    ) {
        return itemRequestService.getAllRequestsExceptAuthor(userId, index, size);
    }

    /**
     * Получение запроса по id.
     * @param requestId - id запроса.
     * @return - запрос с указанным id.
     */

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequestById(@RequestHeader(identificationHeader) @NotNull Long userId,
            @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}