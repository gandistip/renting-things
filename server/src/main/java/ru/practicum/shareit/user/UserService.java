package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, long userId);

    void deleteById(long userId);

    Collection<UserDto> findAll();

    UserDto findById(long userId);
}