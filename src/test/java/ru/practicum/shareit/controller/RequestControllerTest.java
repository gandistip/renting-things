package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private RequestDto requestDto1;
    private RequestDto requestDto2;

    @BeforeEach
    void beforeEach() {

        requestDto1 = RequestDto.builder()
                .id(1L)
                .description("req1 text")
                .created(LocalDateTime.now())
                .build();

        requestDto2 = RequestDto.builder()
                .id(2L)
                .description("req2 text")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void save() throws Exception {
        when(requestService.save(any(RequestDto.class), anyLong())).thenReturn(requestDto1);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(requestDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));

        verify(requestService, times(1)).save(requestDto1, 1L);
    }

    @Test
    void findAllByRequesterId() throws Exception {
        when(requestService.findAllByRequesterId(anyLong())).thenReturn(List.of(requestDto1, requestDto2));

        mvc.perform(get("/requests")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto1, requestDto2))));

        verify(requestService, times(1)).findAllByRequesterId(1L);
    }

    @Test
    void findAllAlien() throws Exception {
        when(requestService.findAllAlien(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestDto1, requestDto2));

        mvc.perform(get("/requests/all")
                .param("from", String.valueOf(0))
                .param("size", String.valueOf(10))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto1, requestDto2))));

        verify(requestService, times(1)).findAllAlien(1L, 0, 10);
    }

    @Test
    void findByRequestId() throws Exception {
        when(requestService.findByRequestId(anyLong(), anyLong())).thenReturn(requestDto1);

        mvc.perform(get("/requests/{requestId}", requestDto1.getId())
                .content(mapper.writeValueAsString(requestDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));

        verify(requestService, times(1)).findByRequestId(1L, 1L);
    }
}