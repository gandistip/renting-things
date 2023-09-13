package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос '{}' от пользователя с id={} создать", requestDto, userId);
        return requestClient.save(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequesterId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запросы пользователя с id={} с вещами получить", userId);
        return requestClient.findAllByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByRequestId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        log.info("Запрос с id={} с вещами получить", userId);
        return requestClient.findByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllAlien(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "999") int size) {
        log.info("Запросы с id={} с предложениями получить", userId);
        return requestClient.findAllAlien(userId, from, size);
    }
}
