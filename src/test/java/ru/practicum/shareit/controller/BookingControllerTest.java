package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    private UserDto userDto;

    private BookingDto bookingDto;

    private BookingDto bookingDto1;

    private BookingDto bookingDto2;


    @BeforeEach
    void beforeEach() {

        userDto = UserDto.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@yandex.ru")
                .build();

        itemDto = ItemDto.builder()
                .requestId(1L)
                .name("hammer")
                .description("steel hammer")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 9, 4, 0, 0))
                .end(LocalDateTime.of(2023, 9, 4, 12, 0))
                .build();

        bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 9, 4, 0, 0))
                .end(LocalDateTime.of(2023, 9, 4, 12, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();

        bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 9, 4, 14, 0))
                .end(LocalDateTime.of(2023, 9, 4, 16, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
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
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

        verify(bookingService, times(1)).findAllByBookerIdByState(1L, "ALL", 0, 10);
    }

    @Test
    void findAllByOwnerIdByState() throws Exception {
        when(bookingService.findAllByOwnerIdByState(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto1, bookingDto2))));

        verify(bookingService, times(1)).findAllByOwnerIdByState(1L, "ALL", 0, 10);
    }

    @Test
    void addBooking() throws Exception {
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
}