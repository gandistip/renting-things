package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class UserStorageMem implements UserStorage {
    private final Map<Long, User> storage = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        user.setId(++id);
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        User upUser = get(id);
        if (!isBlank(user.getName())) {
            upUser.setName(user.getName());
        }
        if (!isBlank(user.getEmail())) {
            if (!user.getEmail().equals(get(id).getEmail())) {
                if (getAll().stream().map(User::getEmail).collect(Collectors.toList())
                        .contains(user.getEmail())) {
                    throw new AlreadyExistException("Пользователь с почтой " + user.getEmail() + " уже существует");
                }
            }
            upUser.setEmail(user.getEmail());
        }
        storage.put(id, upUser);
        return upUser;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User get(Long id) {
        return storage.get(id);
    }
}
