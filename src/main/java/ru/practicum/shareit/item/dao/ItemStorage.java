package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    List<Item> getAll();

    Item get(Long id);
}
