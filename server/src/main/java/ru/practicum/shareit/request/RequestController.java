package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto save(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody RequestDto requestDto) {
        log.info("Запрос '{}' от пользователя с id={} создать", requestDto, userId);
        return requestService.save(requestDto, userId);
    }

    @GetMapping
    public List<RequestDto> findAllByRequesterId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запросы пользователя с id={} с вещами получить", userId);
        return requestService.findAllByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto findByRequestId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        log.info("Запрос с id={} с вещами получить", userId);
        return requestService.findByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAllAlien(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size) {
        log.info("Запросы с id={} с предложениями получить", userId);
        return requestService.findAllAlien(userId, from, size);
    }
}