package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(ItemDtoForCreate itemDto, Long ownerId);

    ItemResponse updateItem(Item itemDto, long ownerId, Long itemId);

    ItemResponse getItemById(Long itemId, Long userId);

    List<ItemResponse> getAllItemsByUserId(Long userId, Integer index, Integer size);

    List<ItemResponse> searchByName(String text, Long userId, Integer index, Integer size);

    CommentResponse createComment(Comment comment, Long itemId, Long userId);
}