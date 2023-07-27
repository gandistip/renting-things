package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class ItemStorageMem implements ItemStorage {
    private final Map<Long, Item> storage = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Item item) {
        item.setId(++id);
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Long id = item.getId();
        Item upItem = get(id);
        if (!isBlank(item.getName())) {
            upItem.setName(item.getName());
        }
        if (!isBlank(item.getDescription())) {
            upItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            upItem.setAvailable(item.getAvailable());
        }
        storage.put(id, upItem);
        return upItem;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Item get(Long id) {
        return storage.get(id);
    }

}
