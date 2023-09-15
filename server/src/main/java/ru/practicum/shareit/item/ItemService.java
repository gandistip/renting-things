package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

public interface ItemService {

    ItemDto findByItemId(long itemId, long userId);

    ItemDto save(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto);

    List<ItemDto> findAllByOwnerId(long userId, int from, int size);

    List<ItemDto> findAllByText(String text, int from, int size);

    CommentDto saveComment(long userId, long itemId, CommentDto commentDto);
}