package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> save(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вещь={} пользователя с id={} добавить", itemDto, userId);
        return ResponseEntity.ok(itemService.save(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Вещь с id={} обновить на вещь={}", itemId, itemDto);
        return ResponseEntity.ok(itemService.update(itemId, userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> findByItemId(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вещь с id={} получить", itemId);
        return ResponseEntity.ok(itemService.findByItemId(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> findAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "999") int size) {
        log.info("Вещи владельца с id={} получить", userId);
        return ResponseEntity.ok(itemService.findAllByOwnerId(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> findAllByText(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "999") int size) {
        log.info("Вещи с подстрокой={} получить", text);
        return ResponseEntity.ok(itemService.findAllByText(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> saveComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Комментарий={} к вещи с id={} добавить", commentDto.getText(), itemId);
        return ResponseEntity.ok(itemService.saveComment(userId, itemId, commentDto));
    }
}