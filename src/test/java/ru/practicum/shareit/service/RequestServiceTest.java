package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.Util;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.RequestRepo;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RequestServiceTest {
    @Autowired
    private RequestService requestService;
    @MockBean
    private RequestRepo requestRepo;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private ItemRepo itemRepo;
    @Mock
    private Util util;

    private Item item;
    private User user1;
    private User user2;
    private Request request1;
    private Request request2;
    private RequestDto requestDto;

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

        request1 = Request.builder()
                .id(1L)
                .description("request descr 1")
                .created(LocalDateTime.now())
                .build();

        request2 = Request.builder()
                .id(2L)
                .description("request descr 2")
                .created(LocalDateTime.now())
                .build();

        requestDto = RequestDto.builder()
                .description("request descr 1")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("item descr")
                .available(true)
                .owner(user1)
                .request(request1)
                .build();
    }

    @Test
    void save() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));
        when(requestRepo.save(any(Request.class))).thenReturn(request1);

        RequestDto requestDtoTest = requestService.save(requestDto, user1.getId());

        assertEquals(requestDtoTest.getId(), request1.getId());
        assertEquals(requestDtoTest.getDescription(), request1.getDescription());

        verify(requestRepo, times(1)).save(any(Request.class));
    }

    @Test
    void findByRequestId() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(requestRepo.existsById(anyLong())).thenReturn(true);
        when(requestRepo.findById(anyLong())).thenReturn(Optional.ofNullable(request1));
        when(itemRepo.findByRequestId(anyLong())).thenReturn(List.of(item));

        RequestDto requestDtoTest = requestService.findByRequestId(user1.getId(), request1.getId());

        assertEquals(requestDtoTest.getId(), request1.getId());
        assertEquals(requestDtoTest.getDescription(), request1.getDescription());
        assertEquals(requestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(requestDtoTest.getItems().get(0).getRequestId(), user1.getId());

        verify(requestRepo, times(1)).findById(anyLong());
    }

    @Test
    void findAllByRequesterId() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(requestRepo.findByRequesterId(anyLong())).thenReturn(List.of(request1));
        when(itemRepo.findAllByRequestIds(anyList())).thenReturn(List.of(item));

        RequestDto requestDtoTest = requestService.findAllByRequesterId(user1.getId()).get(0);

        assertEquals(requestDtoTest.getId(), request1.getId());
        assertEquals(requestDtoTest.getDescription(), request1.getDescription());
        assertEquals(requestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(requestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(requestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(requestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(requestRepo, times(1)).findByRequesterId(anyLong());
    }

    @Test
    void findAllAlien() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(util.getPageIfExist(anyInt(), anyInt())).thenReturn(PageRequest.of(0, 5));
        when(requestRepo.findAllByRequesterIdNot(anyLong(), any(PageRequest.class))).thenReturn(List.of(request1));
        when(itemRepo.findAllByRequestIds(anyList())).thenReturn(List.of(item));

        RequestDto requestDtoTest = requestService.findAllAlien(user1.getId(), 0, 5).get(0);

        assertEquals(requestDtoTest.getId(), request1.getId());
        assertEquals(requestDtoTest.getDescription(), request1.getDescription());
        assertEquals(requestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(requestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(requestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(requestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(requestRepo, times(1)).findAllByRequesterIdNot(anyLong(), any(PageRequest.class));
    }

}