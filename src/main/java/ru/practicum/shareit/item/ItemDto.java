package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;

    @Data
    @Builder
    public static class BookingForItemDto {
        private Long id;
        private Long bookerId;
    }
}