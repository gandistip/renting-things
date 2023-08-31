package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.Util;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @Mock
    private Util util;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private ItemRepo itemRepo;
    @MockBean
    private BookingRepo bookingRepo;

    private User user1;
    private User user2;
    private Item item;
    private ItemDto itemDto;
    private Booking booking1;
    private Booking booking2;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@yandex.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("hammer")
                .description("steel hammer")
                .available(true)
                .owner(user1)
                .build();

        itemDto = ItemMapper.toItemDto(item);

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user1)
                .status(Status.APPROVED)
                .build();

        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user1)
                .status(Status.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 7, 5, 0, 0))
                .end(LocalDateTime.of(2023, 10, 12, 0, 0))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void save() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking1);

        BookingDto bookingOutDtoTest = bookingService.save(bookingDto, anyLong());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserDto(user2));

        verify(bookingRepo, times(1)).save(any(Booking.class));
    }

    @Test
    void saveWrongOwner() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(NotExistException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void saveItemBooked() {
        item.setAvailable(false);

        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void saveNotValidDateEnd() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));

        bookingDto.setEnd(LocalDateTime.of(2020, 11, 11, 11, 11));

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void saveNotValidDateStart() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));

        bookingDto.setStart(LocalDateTime.of(2024, 11, 11, 11, 11));

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void updateStatus() {
        BookingDto bookingOutDtoTest;

        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking2);

        bookingOutDtoTest = bookingService.updateStatus(user1.getId(), item.getId(), true);
        assertEquals(bookingOutDtoTest.getStatus(), Status.APPROVED);

        bookingOutDtoTest = bookingService.updateStatus(user1.getId(), item.getId(), false);
        assertEquals(bookingOutDtoTest.getStatus(), Status.REJECTED);

        verify(bookingRepo, times(2)).save(any(Booking.class));
    }

    @Test
    void updateStatusWrongUser() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking2);

        assertThrows(NotExistException.class, () -> bookingService.updateStatus(user2.getId(), item.getId(), true));
    }

    @Test
    void updateStatusNotValidStatus() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking1);

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(user1.getId(), item.getId(), true));
    }

    @Test
    void findByBookingId() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepo.existsById(anyLong())).thenReturn(true);

        BookingDto bookingOutDtoTest = bookingService.findByBookingId(user1.getId(), booking1.getId());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserDto(user1));

    }

    @Test
    void getBookingByErrorId() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepo.existsById(anyLong())).thenReturn(true);

        assertThrows(NotExistException.class, () -> bookingService.findByBookingId(2L, booking1.getId()));
    }

    @Test
    void findAllByBookerIdByState() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(bookingRepo.findAllByBookerIdOrderByStartDesc(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));

        String state = "ALL";

        List<BookingDto> bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllCurrentByItemBookerId(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "PAST";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "WAITING";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));
    }

    @Test
    void findAllByOwnerIdByState() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(bookingRepo.findAllByItemOwnerIdOrderByStartDesc(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));

        String state = "ALL";

        List<BookingDto> bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllCurrentByItemOwnerId(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "PAST";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "WAITING";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));
    }

    @Test
    void findAllByOwnerIdByStateNotHaveItems() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> bookingService.findAllByOwnerIdByState(user1.getId(), "APPROVED", 5, 10));
    }
}