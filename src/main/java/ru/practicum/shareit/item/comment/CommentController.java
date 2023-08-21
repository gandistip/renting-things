package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{itemId}/comment")
    public CommentDto save(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Комментарий={} к вещи с id={} добавить", commentDto.getText(), itemId);
        return commentService.save(userId, itemId, commentDto);
    }
}
