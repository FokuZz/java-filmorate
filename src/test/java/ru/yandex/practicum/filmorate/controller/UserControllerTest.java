package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    private User.UserBuilder userBuilder;
    private User user;
    private String json;

    private String url = "/users";


    @BeforeEach
    void userBuilder() {
        userBuilder = User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest@yandex.ru")
                .login("UserTest1");
    }

    @Test
    void createStandartUser() throws Exception {
        user = userBuilder.id(1).name("TestName").build();
        json = mapper.writeValueAsString(user);


        when(service.create(user)).thenReturn(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(json))
                .andExpect(jsonPath("$.name", is("TestName")));
    }

    @Test
    void createEmptyNameUser() throws Exception {
        user = userBuilder.id(1).build();
        json = mapper.writeValueAsString(user);

        User expectedUser = userBuilder.id(1).name("UserTest1").build();
        String expectedJson = mapper.writeValueAsString(expectedUser);

        when(service.create(user)).thenReturn(expectedUser);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedJson));
    }


    @Test
    void createEmptyLoginUser() throws Exception {
        user = userBuilder.id(1).login(null).build();
        json = mapper.writeValueAsString(user);

        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage().equals("Login cannot be empty"));
    }

    @Test
    void createEmptyEmailUser() throws Exception {
        user = userBuilder.id(1).email(null).build();
        json = mapper.writeValueAsString(user);

        when(service.create(user)).thenReturn(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage().equals("Email cannot be empty"));
    }

    @Test
    void createFutureBirthDayUser() throws Exception {
        user = userBuilder.id(1).birthday(LocalDate.of(2025, 1, 1)).build();
        json = mapper.writeValueAsString(user);

        when(service.create(user)).thenThrow(new BirthdayInFutureException());
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException()
                                .getMessage()
                                .equals("The date of birth cannot be in the future"));
    }


    @Test
    void updateStandartUser() throws Exception {
        user = userBuilder.name("FirstName").id(1).build();

        when(service.get()).thenReturn(List.of(user));

        user = userBuilder.id(1).name("TestName").build();
        json = mapper.writeValueAsString(user);

        when(service.update(user)).thenReturn(user);
        mockMvc.perform(put(url).contentType(APPLICATION_JSON_UTF8).content(json))
                .andDo(print())
                .andExpect(content().string(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateEmptyUser() throws Exception {
        user = userBuilder.name("FirstName").id(1).build();

        when(service.get()).thenReturn(List.of(user));

        mockMvc.perform(put(url).contentType(APPLICATION_JSON_UTF8).content(""))
                .andDo(print())
                .andExpect(content().string(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStandartFilms() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();

        when(service.get()).thenReturn(List.of(user));
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("UserName1")));
    }

    @Test
    void getEmptyFilms() throws Exception {
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
}