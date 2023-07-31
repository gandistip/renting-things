package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Item create(Item item, Long sharerId) {
        log.info("Получить пользователя с id={}", sharerId);
        User user = Optional.ofNullable(userStorage.get(sharerId))
                .orElseThrow(() -> new NotExistException("Пользователь с id=" + sharerId + " не существует"));
        item.setOwner(user);
        log.info("Создание вещи: {} c id={}", item.getName(), item.getId());
        return itemStorage.create(item);
    }

    public Item update(Item item, Long id, Long sharerId) throws ValidationException {
        get(id);
        if (!Objects.equals(get(id).getOwner().getId(), sharerId)) {
            throw new NotExistException("Вещь с sharerId=" + sharerId + " не существует");
        }
        item.setId(id);
        log.info("Обновление вещи с id={}", id);
        return itemStorage.update(item);
    }

    public Item get(Long id) {
        log.info("Получить вещь с id={}", id);
        return Optional.ofNullable(itemStorage.get(id))
                .orElseThrow(() -> new NotExistException("Вещь с id=" + id + " не существует"));
    }

    public List<Item> getAll(Long sharerId) {
        return itemStorage.getAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), sharerId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (isBlank(text)) {
            return new ArrayList<>();
        }
        return itemStorage.getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
