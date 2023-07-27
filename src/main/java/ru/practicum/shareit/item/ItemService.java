package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemService(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    public Item create(Item item, Long sharerId) {
        item.setOwner(userService.get(sharerId));
        log.info("Создание вещи: {} c id={}", item.getName(), item.getId());
        return itemStorage.create(item);
    }

    public Item update(Item item, Long id, Long sharerId) throws ValidationException {
        get(id);
        if (get(id).getOwner().getId() != sharerId) {
            throw new NotExistException("Вещь с sharerId=" + sharerId + " не существует");
        }
        item.setId(id);
        log.info("Обновление вещи с id={}", id);
        return itemStorage.update(item);
    }

    public Item get(Long id) {
        log.info("Получить вещь с id={}", id);
        return Optional.ofNullable(itemStorage.get(id)).orElseThrow(() -> new NotExistException("Вещь с id=" + id + " не существует"));
    }

    public List<Item> getAll(Long sharerId) {
        return itemStorage.getAll().stream()
                .filter(item -> item.getOwner().getId() == sharerId)
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (isBlank(text)) {
            return new ArrayList<>();
        }
        return itemStorage.getAll().stream()
                .filter(item -> item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
