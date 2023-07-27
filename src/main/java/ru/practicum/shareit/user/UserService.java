package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getEmail());
        }
        if (userStorage.getAll().stream().map(User::getEmail).collect(Collectors.toList())
                .contains(user.getEmail())) {
            throw new AlreadyExistException("Пользователь с почтой " + user.getEmail() + " уже существует");
        }
        userStorage.create(user);
        log.info("Создание пользователя с почтой: {}", user.getEmail());
        return user;
    }

    public User update(User user) throws ValidationException {
        get(user.getId());
        log.info("Обновление пользователя с id={}", user.getId());
        return userStorage.update(user);
    }

    public User get(Long id) {
        log.info("Получить пользователя с id={}", id);
        return Optional.ofNullable(userStorage.get(id))
                .orElseThrow(() -> new NotExistException("Пользователь с id=" + id + " не существует"));
    }

    public void delete(Long id) {
        userStorage.delete(id);
        log.info("Удаление пользователя с id={}", id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }
}
