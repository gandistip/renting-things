package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final ItemRepo itemRepo;
    private final Util util;

    @Transactional
    @Override
    public BookingDto save(BookingDto bookingDto, long userId) {

        Item item = util.getItemIfExist(bookingDto.getItemId());
        User user = util.getUserIfExist(userId);

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);

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

        Booking booking = util.getBookingIfExist(bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotExistException(User.class, "Бронирование отклонить или подтвердить - может только владелец");
        }

        if (approved && booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование - уже подтверждено");
        } else if (approved && !booking.getStatus().equals(Status.APPROVED)) {
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

        util.getUserIfExist(userId);
        Booking booking = util.getBookingIfExist(bookingId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotExistException(User.class, "Бронирование может просмотреть владелец или забронировавший");
        }
    }

    @Transactional
    @Override
    public List<BookingDto> findAllByBookerIdByState(long userId, String stateString, int from, int size) {

        PageRequest page = util.getPageIfExist(from, size);
        State state = util.getStateIfExist(stateString);
        util.getUserIfExist(userId);

        Page<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepo.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllCurrentByItemBookerId(userId, page);
                break;
            case PAST:
                bookings = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                break;
            default:
                bookings = Page.empty();
        }

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDto> findAllByOwnerIdByState(long userId, String stateString, int from, int size) {

        PageRequest page = util.getPageIfExist(from, size);
        State state = util.getStateIfExist(stateString);
        util.getUserIfExist(userId);

        if (itemRepo.findByOwnerId(userId).isEmpty()) {
            throw new ValidationException("Пользователь не является владельцем");
        }

        Page<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllCurrentByItemOwnerId(userId, page);
                break;
            case PAST:
                bookings = bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                break;
            default:
                bookings = Page.empty();
        }

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}