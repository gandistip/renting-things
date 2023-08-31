package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void setUp() {

        userDto1 = UserDto.builder()
                .id(1L)
                .name("userName1")
                .email("u1@ya.ru")
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .name("userName2")
                .email("u2@ya.ru")
                .build();
    }

    @Test
    void save() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).save(userDto1);
    }

    @Test
    void update() throws Exception {
        when(userService.update(any(UserDto.class), anyLong())).thenReturn(userDto2);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto2.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto2.getEmail()), String.class));

        verify(userService, times(1)).update(userDto2, 1L);
    }

    @Test
    void findById() throws Exception {
        when(userService.findById(1L)).thenReturn(userDto1);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void findAll() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto1, userDto2))));

        verify(userService, times(1)).findAll();
    }

}
