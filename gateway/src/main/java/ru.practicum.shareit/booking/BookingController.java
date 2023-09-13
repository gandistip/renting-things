package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        log.info("Бронирование для пользователя с id={} создать", userId);
        return bookingClient.save(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam Boolean approved) {
        log.info("Бронирование с id={} подтвердить/отклонить: {}", bookingId, approved);
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findByBookingId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        log.info("Бронирование с id={} получить", bookingId);
        return bookingClient.findByBookingId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBookerIdByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "999") int size) {
        BookingState bookingState = BookingState.from(state).orElseThrow(() -> new ValidationException("Unknown state: " + state));
        log.info("Бронирования пользователя с id={} и статусом={} получить", userId, state);
        return bookingClient.findAllByBookerIdByState(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerIdByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "999") int size) {
        BookingState bookingState = BookingState.from(state).orElseThrow(() -> new ValidationException("Unknown state: " + state));
        log.info("Бронирования владельца с id={} и статусом={} получить", userId, state);
        return bookingClient.findAllByOwnerIdByState(userId, bookingState, from, size);
    }
}