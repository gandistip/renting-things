package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping()
    public UserDto save(
            @RequestBody @Valid UserDto userDto) {
        log.info("Пользователя={} создать", userDto);
        return userService.save(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @RequestBody UserDto userDto,
            @PathVariable long userId) {
        log.info("Пользователя с id={} обновить на пользователя={}", userId, userDto);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(
            @PathVariable long userId) {
        log.info("Пользователя с id={} удалить", userId);
        userService.deleteById(userId);
    }

    @GetMapping()
    public List<UserDto> findAll() {
        log.info("Пользователей получить");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(
            @PathVariable long userId) {
        log.info("Пользователя с id={} получить", userId);
        return userService.findById(userId);
    }
}