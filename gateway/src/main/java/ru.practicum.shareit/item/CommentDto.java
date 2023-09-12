package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    public Long id;

    @NotBlank
    private String text;

    private LocalDateTime created;

    private String authorName;
}
