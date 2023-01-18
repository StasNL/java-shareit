package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(Item item, Long ownerId);

    ItemResponse updateItem(Item item, long ownerId, Long itemId);

    ItemResponse getItemById(Long itemId, Long userId);

    List<ItemResponse> getAllItemsByUserId(Long userId);

    List<ItemResponse> searchByName(String text);

    CommentResponse createComment(Comment comment, Long itemId, Long userId);
}
