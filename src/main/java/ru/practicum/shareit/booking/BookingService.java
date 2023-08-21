package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.State;

import java.util.List;


public interface BookingService {

    BookingDto save(BookingDtoIn bookingDtoIn, long userId);

    BookingDto updateStatus(long userId, long bookingId, boolean approved);

    BookingDto findByBookingId(long userId, long bookingId);

    List<BookingDto> findAllByBookerIdByState(long userId, State state);

    List<BookingDto> findAllByOwnerIdByState(long userId, State state);
}