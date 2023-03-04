package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/items")
public class ItemController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(@RequestBody ItemDtoForCreate itemDto,
                                   @RequestHeader(IDENTIFICATION_HEADER) Long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestBody @Valid Item item,
                                   @RequestHeader(IDENTIFICATION_HEADER) Long ownerId,
                                   @PathVariable Long itemId) {
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@RequestHeader(IDENTIFICATION_HEADER) Long userId,
                                    @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponse> getAllItemsByUserId(
            @RequestHeader(IDENTIFICATION_HEADER) Long ownerId,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer index,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return itemService.getAllItemsByUserId(ownerId, index, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchByName(
            @RequestHeader(IDENTIFICATION_HEADER) Long userId,
            @RequestParam(name = "text") String text,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer index,
            @RequestParam(name = "size", required = false) Integer size) {
        return itemService.searchByName(text, userId, index, size);
    }

    @Transactional
    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestBody Comment comment,
                                         @PathVariable Long itemId,
                                         @RequestHeader(IDENTIFICATION_HEADER) Long userId) {
        return itemService.createComment(comment, itemId, userId);
    }
}