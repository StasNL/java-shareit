package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID_FROM_HEADER;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(@RequestBody @Valid Item item,
                                   @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long ownerId) {
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestBody @Valid Item item,
                                   @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long ownerId,
                                   @PathVariable Long itemId) {
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId,
                                    @PathVariable @NotNull Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponse> getAllItemsByUserId(@RequestHeader(USER_ID_FROM_HEADER) @NotNull Long ownerId) {
        return itemService.getAllItemsByUserId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchByName(@RequestParam(name = "text") @NotNull @NotBlank String text) {
        return itemService.searchByName(text);
    }

    @Transactional
    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestBody @Valid Comment comment,
                                         @PathVariable @NotNull Long itemId,
                                         @RequestHeader(USER_ID_FROM_HEADER) @NotNull Long userId) {
        return itemService.createComment(comment, itemId, userId);
    }
}
