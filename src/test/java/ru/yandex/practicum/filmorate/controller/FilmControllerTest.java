package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.CookieResultMatchers;
import ru.yandex.practicum.filmorate.exception.RelaseDateEarlyThanNecessaryException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService service;

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    String url = "/films";
    Film.FilmBuilder filmBuilder;

    Film film;
    String json;

    @BeforeEach
    void filmBuilder() {
        filmBuilder = Film.builder()
                .name("JunitName")
                .description("JunitDescription")
                .releaseDate(LocalDate.of(2020,1,1))
                .duration(90);
    }

    @Test
    void createStandardFilm() throws Exception {
    film = filmBuilder.id(1).build();
    json = mapper.writeValueAsString(film);

    when(service.createFilm(film)).thenReturn(film);
    mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string(json));

    }

    @Test
    void createEmptyNameFilm() throws Exception {
        film = filmBuilder.id(1).name(null).build();
        json = mapper.writeValueAsString(film);


        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                result.getResolvedException().getMessage().equals("Name cannot be empty"));

    }

    @Test
    void createDescriptionMoreThan200Film() throws Exception {
        String symbols = "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        symbols = symbols + symbols + symbols;
        film = filmBuilder.id(1).description(symbols).build();
        json = mapper.writeValueAsString(film);

        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage().equals("Max 200 letters"));
    }

    @Test
    void createDateReleaseBeforeThan1895Film() throws Exception {
        LocalDate past = LocalDate.of(1890,1,1);
        film = filmBuilder.id(1).releaseDate(past).build();
        json = mapper.writeValueAsString(film);

        when(service.createFilm(film)).thenThrow(new RelaseDateEarlyThanNecessaryException());
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> result
                        .getResolvedException()
                        .getMessage().equals("Release date — no earlier than December 28, 1895"));
    }

    @Test
    void createNegativeDurationFilm() throws Exception {
        film = filmBuilder.id(1).duration(-10).build();
        json = mapper.writeValueAsString(film);

        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage().equals("Only positive duration"));
    }
    @Test
    void updateStandardFilm() throws Exception {
        film = filmBuilder.name("Filmname1").id(1).build();

        when(service.getFilms()).thenReturn(List.of(film));

        film = filmBuilder.id(1).name("JunitUpdateName").build();
        json = mapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenReturn(film);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(content().string(json))
                .andExpect(status().isOk());
    }
    @Test
    void updateEmptyFilm() throws Exception {
        film = filmBuilder.name("Filmname1").id(1).build();

        when(service.getFilms()).thenReturn(List.of(film));

        when(service.updateFilm(film)).thenReturn(film);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(""))
                .andDo(print())
                .andExpect(content().string(""))
                .andExpect(status().isBadRequest());
    }


    @Test
    void standardGetFilms() throws Exception {
        film = filmBuilder.name("Film name1").id(1).build();

        when(service.getFilms()).thenReturn(List.of(film));
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Film name1")));
    }
    @Test
    void emptyGetFilms() throws Exception {
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

}