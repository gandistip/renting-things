package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    ItemDto findByItemId(long itemId, long userId);

    ItemDto save(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto);

    List<ItemDto> findAllByOwnerId(long userId);

    List<ItemDto> findAllByText(String text);
}