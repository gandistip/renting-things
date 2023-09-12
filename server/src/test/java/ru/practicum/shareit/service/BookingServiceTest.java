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
    @MockBean
    private BookingRepo bookingRepo;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private ItemRepo itemRepo;

    @Mock
    private Util util;

    private Item item;
    private User user1;
    private User user2;
    private Booking booking1;
    private Booking booking2;
    private ItemDto itemDto;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {

        user1 = User.builder()
                .id(1L)
                .name("userName1")
                .email("u1@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName2")
                .email("u2@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("item descr")
                .available(true)
                .owner(user1)
                .build();

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
                .start(LocalDateTime.of(2023, 1, 1, 0, 0))
                .end(LocalDateTime.of(2023, 2, 1, 0, 0))
                .status(Status.APPROVED)
                .build();

        itemDto = ItemMapper.toItemDto(item);
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
    void findAllByBookerIdByState() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(0, 5));
        when(bookingRepo.findAllByBookerIdOrderByStartDesc(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));

        String state = "ALL";

        List<BookingDto> bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllCurrentByItemBookerId(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "PAST";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "WAITING";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.findAllByBookerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));
    }

    @Test
    void findAllByOwnerIdByState() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(0, 5));
        when(bookingRepo.findAllByItemOwnerIdOrderByStartDesc(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));

        String state = "ALL";

        List<BookingDto> bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllCurrentByItemOwnerId(
                anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "PAST";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "WAITING";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));

        when(bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.findAllByOwnerIdByState(user1.getId(), state, 0, 5);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserDto(user1));
    }

    @Test
    void bookerIsNotOwner() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(NotExistException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void bookingIsBusy() {
        item.setAvailable(false);

        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void invalidDateEnd() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));

        bookingDto.setEnd(LocalDateTime.of(2022, 1, 1, 0, 0));

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void invalidDateStart() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));

        bookingDto.setStart(LocalDateTime.of(2024, 1, 1, 0, 0));

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, anyLong()));
    }

    @Test
    void statusInvalidUser() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking2);

        assertThrows(NotExistException.class, () -> bookingService.updateStatus(user2.getId(), item.getId(), true));
    }

    @Test
    void invalidStatus() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking1);

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(user1.getId(), item.getId(), true));
    }

    @Test
    void ownerNotHaveItems() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> bookingService.findAllByOwnerIdByState(
                user1.getId(), "APPROVED", 0, 5));
    }

    @Test
    void invalidBookingId() {
        when(bookingRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepo.existsById(anyLong())).thenReturn(true);

        assertThrows(NotExistException.class, () -> bookingService.findByBookingId(2L, booking1.getId()));
    }

}