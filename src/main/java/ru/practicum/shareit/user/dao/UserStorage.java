package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Long id);

    List<User> getAll();

    User get(Long id);
}
