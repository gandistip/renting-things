package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoForItemDto lastBooking;
    private BookingDtoForItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

    @Data
    @Builder
    public static class BookingDtoForItemDto {
        private Long id;
        private Long bookerId;
    }
}