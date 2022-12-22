package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;

import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private long id;
    private final HashMap<Long, Item> itemsInMemory = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        InMemoryUserStorage.checkUserById(item.getOwnerId());
        long itemId = ++id;
        item.setId(itemId);
        itemsInMemory.put(itemId, item);
        return itemsInMemory.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        Long itemId = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        long ownerId = item.getOwnerId();
        Boolean available = item.getAvailable();

        InMemoryUserStorage.checkUserById(ownerId);
        checkItemById(item.getId());
        Item itemToUpd = itemsInMemory.get(itemId);
        if (ownerId != itemToUpd.getOwnerId())
            throw new NotFoundException("Вещь с указанным id у данного собственника не найдена.");

        if (name != null)
            itemToUpd.setName(name);
        if (description != null)
            itemToUpd.setDescription(description);
        if (available != null)
            itemToUpd.setAvailable(available);
        itemsInMemory.put(itemId, itemToUpd);
        return itemsInMemory.get(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        checkItemById(itemId);
        return itemsInMemory.get(itemId);
    }

    @Override
    public List<Item> getAllItemsByUserId(Long ownerId) {
        InMemoryUserStorage.checkUserById(ownerId);
        List<Item> itemsOfUser = new ArrayList<>();
        for (Item item : itemsInMemory.values())
            if (item.getOwnerId() == ownerId)
                itemsOfUser.add(item);
        return itemsOfUser;
    }

    @Override
    public List<Item> searchByName(String text) {
        List<Item> items = new ArrayList<>();
        if (!text.isEmpty()) {
            for (Item item : itemsInMemory.values()) {
                String name = item.getName();
                String description = item.getDescription();
                Boolean available = item.getAvailable();

                if (available && (name.toLowerCase().contains(text.toLowerCase()) ||
                        description.toLowerCase().contains(text.toLowerCase())))
                    items.add(item);
            }
        }
        return items;
    }

    private void checkItemById(Long itemId) {
        if (!itemsInMemory.containsKey(itemId))
            throw new NotFoundException(useType(ITEM));
    }
}
