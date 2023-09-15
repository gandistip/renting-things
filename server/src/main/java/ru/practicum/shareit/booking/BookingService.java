package ru.practicum.shareit.booking;

import java.util.List;


public interface BookingService {

    BookingDto save(BookingDto bookingDtoIn, long userId);

    BookingDto updateStatus(long userId, long bookingId, boolean approved);

    BookingDto findByBookingId(long userId, long bookingId);

    List<BookingDto> findAllByBookerIdByState(long userId, String state, int from, int size);

    List<BookingDto> findAllByOwnerIdByState(long userId, String state, int from, int size);
}