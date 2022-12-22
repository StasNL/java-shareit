package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdate;

public class ItemMapper {
    public static Item itemCreateToItem(ItemCreate itemCreate, Long ownerId) {
        return Item.builder()
                .name(itemCreate.getName())
                .description(itemCreate.getDescription())
                .ownerId(ownerId)
                .available(itemCreate.getAvailable())
                .build();
    }

    public static Item itemUpdateToItem(ItemUpdate itemUpdate, Long itemId, Long ownerId) {
        return Item.builder()
                .id(itemId)
                .name(itemUpdate.getName())
                .description(itemUpdate.getDescription())
                .ownerId(ownerId)
                .available(itemUpdate.getAvailable())
                .build();
    }

    public static ItemResponse itemToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
