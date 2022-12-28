package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(@RequestBody @Valid Item item,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        item = itemService.createItem(item, ownerId);
        item = itemService.getItemById(item.getId());
        return ItemMapper.itemToItemResponse(item);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestBody @Valid Item item,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                   @PathVariable Long itemId) {
        if (itemId != null)
            item.setId(itemId);

        item = itemService.updateItem(item, ownerId);
        item = itemService.getItemById(item.getId());
        return ItemMapper.itemToItemResponse(item);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@PathVariable @NotNull Long itemId) {
        Item item = itemService.getItemById(itemId);
        return ItemMapper.itemToItemResponse(item);
    }

    @GetMapping
    public List<ItemResponse> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        List<Item> items = itemService.getAllItemsByUserId(ownerId);
        return convertListForResponse(items);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchByName(@RequestParam(name = "text") @NotNull @NotBlank String text) {
        List<Item> items = itemService.searchByName(text);
        return convertListForResponse(items);
    }

    private List<ItemResponse> convertListForResponse(List<Item> items) {
        return items.stream()
                .map(ItemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }
}
