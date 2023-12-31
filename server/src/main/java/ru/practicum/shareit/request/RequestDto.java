package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
