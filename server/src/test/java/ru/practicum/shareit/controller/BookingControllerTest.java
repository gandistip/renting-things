package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private UserDto userDto;
    private ItemDto itemDto;
    private BookingDto bookingDto;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;

    @BeforeEach
    void beforeEach() {

        userDto = UserDto.builder()
                .id(1L)
                .name("userName")
                .email("email@ya.ru")
                .build();

        itemDto = ItemDto.builder()
                .requestId(1L)
                .name("itemName")
                .description("item Descr")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 10, 1, 0, 0))
                .end(LocalDateTime.of(2023, 10, 10, 0, 0))
                .build();

        bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 1, 0, 0))
                .end(LocalDateTime.of(2023, 10, 10, 0, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();

        bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 10, 20, 0, 0))
                .end(LocalDateTime.of(2023, 10, 30, 0, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void save() throws Exception {
        when(bookingService.save(any(BookingDto.class), anyLong())).thenReturn(bookingDto1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).save(bookingDto, 1L);
    }

    @Test
    void updateStatus() throws Exception {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto1);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).updateStatus(1L, 1L, true);
    }

    @Test
    void findByBookingId() throws Exception {
        when(bookingService.findByBookingId(anyLong(), anyLong())).thenReturn(bookingDto1);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).findByBookingId(1L, 1L);
    }

    @Test
    void findAllByBookerIdByState() throws Exception {
        when(bookingService.findAllByBookerIdByState(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

        verify(bookingService, times(1)).findAllByBookerIdByState(1L, "ALL", 0, 5);
    }

    @Test
    void findAllByOwnerIdByState() throws Exception {
        when(bookingService.findAllByOwnerIdByState(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

        verify(bookingService, times(1)).findAllByOwnerIdByState(1L, "ALL", 0, 5);
    }

}