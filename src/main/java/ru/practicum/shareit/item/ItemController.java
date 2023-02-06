package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/items")
public class ItemController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(@RequestBody @Valid ItemDtoForCreate itemDto,
                                   @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestBody @Valid Item item,
                                   @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long ownerId,
                                   @PathVariable Long itemId) {
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
                                    @PathVariable @NotNull Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponse> getAllItemsByUserId(
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long ownerId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false) @Positive Integer size
    ) {
        return itemService.getAllItemsByUserId(ownerId, index, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchByName(
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
            @RequestParam(name = "text") @NotNull String text,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false) @Positive Integer size) {
        return itemService.searchByName(text, userId, index, size);
    }

    @Transactional
    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestBody @Valid Comment comment,
                                         @PathVariable @NotNull Long itemId,
                                         @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId) {
        return itemService.createComment(comment, itemId, userId);
    }
}