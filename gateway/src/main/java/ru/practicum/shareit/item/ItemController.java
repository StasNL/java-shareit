package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.itemDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.NewItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/items")
public class ItemController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid NewItemDto item,
                                     @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long ownerId) {
        return itemClient.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemDto item,
                                   @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long ownerId,
                                   @PathVariable Long itemId) {
        return itemClient.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
                                    @PathVariable @NotNull Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long ownerId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size
    ) {
        return itemClient.getAllItemsByUserId(ownerId, index, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
            @RequestParam(name = "text") @NotNull String text,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size) {
        return itemClient.searchByName(text, userId, index, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentDto comment,
                                         @PathVariable @NotNull Long itemId,
                                         @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId) {
        return itemClient.createComment(comment, itemId, userId);
    }
}