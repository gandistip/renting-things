package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .build();
        return bookingDto;
    }

    public static BookingDtoItem toBookingDtoItem(Booking booking) {
        BookingDtoItem bookingDtoItem = BookingDtoItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
        return bookingDtoItem;
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn) {
        Booking booking = Booking.builder()
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .build();
        if (bookingDtoIn.getStatus() == null) {
            booking.setStatus(Status.WAITING);
        } else {
            booking.setStatus(bookingDtoIn.getStatus());
        }
        return booking;
    }

}