package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchByName(String text);
}
