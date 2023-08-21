package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        log.info("Бронирование для пользователя с id={} создать", userId);
        return bookingService.save(bookingDtoIn, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Бронирование с id={} подтвердить/отклонить: {}", bookingId, approved);
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findByBookingId(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Бронирование с id={} получить", bookingId);
        return bookingService.findByBookingId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByBookerIdByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Бронирования пользователя с id={} и статусом {} получить", userId, state);
        return bookingService.findAllByBookerIdByState(userId, BookingServiceImpl.stringToState(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerIdByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Бронирования владельца с id={} и статусом {} получить", userId, state);
        return bookingService.findAllByOwnerIdByState(userId, BookingServiceImpl.stringToState(state));
    }
}