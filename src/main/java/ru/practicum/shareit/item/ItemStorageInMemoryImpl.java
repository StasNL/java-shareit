package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;

import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorageInMemoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ItemStorageInMemoryImpl implements ItemStorage {
    private long id;
    private final HashMap<Long, Item> itemsInMemory = new HashMap<>();

    @Override
    public Item createItem(Item item, long ownerId) {
        User owner = UserStorageInMemoryImpl.checkUserById(ownerId);
        long itemId = ++id;
        item.setId(itemId);
        item.setOwner(owner);
        itemsInMemory.put(itemId, item);
        return itemsInMemory.get(itemId);
    }

    @Override
    public Item updateItem(Item item, long ownerId) {
        Long itemId = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();

        UserStorageInMemoryImpl.checkUserById(ownerId);
        checkItemById(item.getId());
        Item itemToUpd = itemsInMemory.get(itemId);
        if (ownerId != itemToUpd.getOwner().getId())
            throw new NotFoundException("Вещь с указанным id у данного собственника не найдена.");

        if (name != null) itemToUpd.setName(name);
        if (description != null) itemToUpd.setDescription(description);
        if (available != null) itemToUpd.setAvailable(available);
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
        UserStorageInMemoryImpl.checkUserById(ownerId);
        List<Item> itemsOfUser = new ArrayList<>();
        for (Item item : itemsInMemory.values()) {
            long ownerForComparison = item.getOwner().getId();
            if (ownerForComparison == ownerId) itemsOfUser.add(item);
        }
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

                if (available && (name.toLowerCase().contains(text.toLowerCase()) || description.toLowerCase().contains(text.toLowerCase())))
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
