package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequest {
    private Long id;
    private Long customerId;
    private String description;
}
