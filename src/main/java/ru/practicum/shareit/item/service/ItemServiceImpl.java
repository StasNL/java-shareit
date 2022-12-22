package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    public Item createItem(Item item) {
        return itemStorage.createItem(item);
    }

    public Item updateItem(Item item) {
        return itemStorage.updateItem(item);
    }

    public Item getItemById(Long itemId) {
        return itemStorage.getItemById(itemId);
    }

    public List<Item> getAllItemsByUserId(Long userId) {
        return itemStorage.getAllItemsByUserId(userId);
    }

    public List<Item> searchByName(String text) {
        return itemStorage.searchByName(text);
    }
}
