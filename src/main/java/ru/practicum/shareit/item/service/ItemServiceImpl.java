package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    public Item createItem(Item item, Long ownerId) {
        checkCreatingItem(item);
        return itemStorage.createItem(item, ownerId);
    }

    public Item updateItem(Item item, long ownerId) {
        return itemStorage.updateItem(item, ownerId);
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

    private void checkCreatingItem(Item item) {
        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null ||
                item.getName().isEmpty() || item.getDescription().isEmpty())
            throw new BadRequestException("Неверные поля при создании Item");
    }
}
