package ru.practicum.shareit.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.Util;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepo;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @MockBean
    private ItemRepo itemRepo;
    @MockBean
    private CommentRepo commentRepo;
    @MockBean
    private BookingRepo bookingRepo;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private RequestRepo requestRepo;
    @Mock
    private Util util;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private Request request;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@yandex.ru")
                .build();

        request = Request.builder()
                .id(1L)
                .description("text request description")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("hammer")
                .description("steel hammer")
                .available(true)
                .owner(user)
                .request(request)
                .build();

        itemDto = ItemMapper.toItemDto(item);

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .created(LocalDateTime.now())
                .text("text")
                .build();

        commentDto = CommentMapper.toCommentDto(comment);

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addItem() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepo.existsById(anyLong())).thenReturn(true);
        when(requestRepo.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        when(itemRepo.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.save(user.getId(), itemDto);

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepo, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepo.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.update(item.getId(), user.getId(), itemDto);

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepo, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemNotBelongUser() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemRepo.findByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NotExistException.class, () -> itemService.update(item.getId(), user.getId(), itemDto));
    }

    @Test
    void getItemById() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findNextBooking(List.of(item.getId()), Status.APPROVED, PageRequest.of(0,1))).thenReturn(List.of(booking1));
        when(bookingRepo.findLastBooking(List.of(item.getId()), Status.APPROVED, PageRequest.of(0,1))).thenReturn(List.of(booking2));
        when(commentRepo.findByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.findByItemId(item.getId(), user.getId());

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepo, times(1)).findById(anyLong());
    }

    @Test
    void getItemsUser() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepo.findNextBooking(List.of(item.getId()), Status.APPROVED, PageRequest.of(0,1))).thenReturn(List.of(booking1));
        when(bookingRepo.findLastBooking(List.of(item.getId()), Status.APPROVED, PageRequest.of(0,1))).thenReturn(List.of(booking2));
        when(commentRepo.findByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.findAllByOwnerId(user.getId(), 5, 10).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepo, times(1)).findByOwnerId(anyLong());
    }

    @Test
    void searchItem() {
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(itemRepo.search(anyString(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(item)));

        ItemDto itemDtoTest = itemService.findAllByText("text", 5, 10).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepo, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void searchItemEmptyText() {
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));

        List<ItemDto> itemDtoTest = itemService.findAllByText("", 5, 10);

        assertTrue(itemDtoTest.isEmpty());

        verify(itemRepo, times(0)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void addComment() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(booking1);
        when(commentRepo.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDtoTest = itemService.saveComment(user.getId(), item.getId(), commentDto);

        assertEquals(1, comment.getId());
        assertEquals(commentDtoTest.getText(), comment.getText());
        assertEquals(commentDtoTest.getAuthorName(), comment.getAuthor().getName());

        verify(commentRepo, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentUserNotBookingItem() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(null);

        assertThrows(ValidationException.class, () -> itemService.saveComment(user.getId(), item.getId(), commentDto));
    }
}