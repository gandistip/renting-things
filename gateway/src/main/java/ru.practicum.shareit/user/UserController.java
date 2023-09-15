package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @Valid @RequestBody UserDto userDto) {
        log.info("Пользователя={} создать", userDto);
        return userClient.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable long userId,
            @RequestBody UserDto userDto) {
        log.info("Пользователя с id={} обновить на пользователя={}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(
            @PathVariable long userId) {
        log.info("Пользователя с id={} удалить", userId);
        return userClient.deleteById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Пользователей получить");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(
            @PathVariable long userId) {
        log.info("Пользователя с id={} получить", userId);
        return userClient.findById(userId);
    }
}