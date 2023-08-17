package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepo.save(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, long userId) {
        User updateUser = userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        User user = UserMapper.toUser(userDto);

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            for (User user1 : userRepo.findByEmail(user.getEmail())) {
                if (user1.getId() != userId) {
                    throw new AlreadyExistException("Такая почта уже используется");
                }
            }
            updateUser.setEmail(user.getEmail());
        }

        userRepo.save(updateUser);
        return UserMapper.toUserDto(updateUser);
    }

    @Transactional
    @Override
    public void deleteById(long userId) {
        userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        userRepo.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepo.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto findById(long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        return UserMapper.toUserDto(user);
    }
}
