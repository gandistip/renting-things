package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jBookingDtoTest;
    @Autowired
    private JacksonTester<BookingDto> jBookingDtoShortTest;
    @Autowired
    private JacksonTester<BookingDto> jBookingDtoBigTest;

    @Test
    void testBookingDto() throws Exception {

        LocalDateTime start = LocalDateTime.of(2023, 8, 4, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 4, 12, 0);

        UserDto user = UserDto.builder()
                .id(1L)
                .name("user")
                .email("u@ya.ru")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("itemName")
                .description("item descr")
                .available(true)
                .build();

        BookingDto bookingDtoOut = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .booker(user)
                .item(itemDto)
                .build();

        JsonContent<BookingDto> result = jBookingDtoTest.write(bookingDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("u@ya.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @Test
    void testBookingDtoShort() throws Exception {

        LocalDateTime start = LocalDateTime.of(2023, 8, 4, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 4, 12, 0);

        BookingDto bookingDtoShort = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .bookerId(1L)
                .build();

        JsonContent<BookingDto> result = jBookingDtoShortTest.write(bookingDtoShort);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    @Test
    void testBookingDtoBig() throws Exception {

        LocalDateTime start = LocalDateTime.of(2023, 8, 4, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 4, 12, 0);

        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .build();

        JsonContent<BookingDto> result = jBookingDtoBigTest.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}