package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> save(
            @RequestBody @Valid RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос '{}' от пользователя с id={} создать", requestDto, userId);
        return ResponseEntity.ok(requestService.save(requestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> findAllByRequesterId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запросы пользователя с id={} с вещами получить", userId);
        return ResponseEntity.ok(requestService.findAllByRequesterId(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> findByRequestId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        log.info("Запрос с id={} с вещами получить", userId);
        return ResponseEntity.ok(requestService.findByRequestId(userId, requestId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> findAllAlien(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "999") int size) {
        log.info("Запросы с id={} с предложениями получить", userId);
        return ResponseEntity.ok(requestService.findAllAlien(userId, from, size));
    }
}