package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long ownerId);

    Item updateItem(Item item, long ownerId);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchByName(String text);
}
