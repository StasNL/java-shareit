package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemResponse itemToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemResponse itemToItemResponse(Item item,
                                                  Booking last,
                                                  Booking next,
                                                  List<CommentResponse> comments) {
        ItemResponse.Booking lastBooking = null;
        ItemResponse.Booking nextBooking = null;

        if (last != null)
            lastBooking = new ItemResponse.Booking(last.getId(),
                    last.getBooker().getId());
        if (next != null)
            nextBooking = new ItemResponse.Booking(next.getId(),
                    next.getBooker().getId());

        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static ItemResponse itemToItemResponseWithComments(Item item, List<CommentResponse> comments) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public static CommentResponse commentToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthorName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentResponse> commentsToCommentsResponse(List<Comment> comments) {
        List<CommentResponse> response = new ArrayList<>();
        if (comments.size() != 0)
            response = comments.stream()
                    .map(ItemMapper::commentToCommentResponse)
                    .collect(Collectors.toList());
        return response;
    }
}
