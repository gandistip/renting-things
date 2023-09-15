package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto save(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Вещь={} пользователя с id={} добавить", itemDto, userId);
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Вещь с id={} обновить на вещь={}", itemId, itemDto);
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findByItemId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {
        log.info("Вещь с id={} получить", itemId);
        return itemService.findByItemId(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> findAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size) {
        log.info("Вещи владельца с id={} получить", userId);
        return itemService.findAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findAllByText(
            @RequestParam String text,
            @RequestParam int from,
            @RequestParam int size) {
        log.info("Вещи с подстрокой={} получить", text);
        return itemService.findAllByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody CommentDto commentDto) {
        log.info("Комментарий={} к вещи с id={} добавить", commentDto.getText(), itemId);
        return itemService.saveComment(userId, itemId, commentDto);
    }
}