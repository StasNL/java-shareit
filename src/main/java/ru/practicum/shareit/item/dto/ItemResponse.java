package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemResponse.Booking lastBooking;
    private ItemResponse.Booking nextBooking;
    private Long requestId;
    private List<CommentResponse> comments;

    @Data
    @AllArgsConstructor
    public static class Booking {
        private Long id;
        private Long bookerId;
    }
}
