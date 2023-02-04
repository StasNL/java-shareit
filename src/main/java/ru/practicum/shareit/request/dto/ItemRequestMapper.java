package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestResponse itemRequestToItemRequestResponse(ItemRequest itemRequest) {
        List<Item> items = itemRequest.getItems();
        List<ItemResponse> itemResponses = new ArrayList<>();
        if (items != null && items.size() != 0) {
            itemResponses = ItemMapper.itemToItemResponse(items);
        }

        return ItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemResponses)
                .build();
    }

    public static List<ItemRequestResponse> itemRequestToItemRequestResponse(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestResponse)
                .collect(Collectors.toList());
    }

}
