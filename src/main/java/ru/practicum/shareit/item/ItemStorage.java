package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long ownerId);

    List<Item> searchByName(String text);
}
