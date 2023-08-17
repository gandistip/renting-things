package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;

    @Transactional
    @Override
    public BookingDto save(BookingDtoIn bookingDtoIn, long userId) {

        Item item = itemRepo.findById(bookingDtoIn.getItemId()).orElseThrow(() -> new NotExistException("Вещь - не найдена"));
        User user = userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));

        Booking booking = BookingMapper.toBooking(bookingDtoIn);

        booking.setItem(item);
        booking.setBooker(user);

        if (item.getOwner().equals(user)) {
            throw new NotExistException(User.class, "Бронирование своей вещи - недоступно");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Бронирование данной вещи - недоступно");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Бронирование на отрицательный срок - недоступно");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Бронирование на нулевой срок - недоступно");
        }

        bookingRepo.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateStatus(long userId, long bookingId, boolean approved) {

        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> new NotExistException("Бронирование - не найдено"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotExistException(User.class, "Бронирование отклонить или подтвердить - может только владелец");
        }

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Бронирование - уже подтверждено");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepo.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto findByBookingId(long userId, long bookingId) {

        userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> new NotExistException("Бронирование - не найдено"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotExistException(User.class, "Бронирование может просмотреть владелец или забронировавший");
        }
    }

    @Transactional
    @Override
    public List<BookingDto> findAllByBookerIdByState(long userId, String state) {

        userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));

        List<Booking> bookings = null;

        switch (stringToState(state)) {
            case ALL:
                bookings = bookingRepo.findAllByBookerIdOrderByStartDesc(
                        userId);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, Status.REJECTED);
                break;
        }

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDto> findAllByOwnerIdByState(long userId, String state) {

        userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));

        if (itemRepo.findByOwnerId(userId).isEmpty()) {
            throw new ValidationException("Пользователь не является владельцем");
        }

        List<Booking> bookings = null;

        switch (stringToState(state)) {
            case ALL:
                bookings = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(
                        userId);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, Status.REJECTED);
                break;
        }

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public static State stringToState(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }

}