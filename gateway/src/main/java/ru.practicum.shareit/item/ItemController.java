package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вещь={} пользователя с id={} добавить", itemDto, userId);
        return itemClient.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Вещь с id={} обновить на вещь={}", itemId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findByItemId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId) {
        log.info("Вещь с id={} получить", itemId);
        return itemClient.findByItemId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "999") @Positive int size) {
        log.info("Вещи владельца с id={} получить", userId);
        return itemClient.findAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllByText(
            @RequestParam("text") String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "999") @Positive int size) {
        log.info("Вещи с подстрокой={} получить", text);
        return itemClient.findAllByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Комментарий={} к вещи с id={} добавить", commentDto.getText(), itemId);
        return itemClient.saveComment(userId, itemId, commentDto);
    }
}