package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc()
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    private String url = "/films";
    private Film.FilmBuilder filmBuilder;

    private Film film;

    private final User user = User.builder()
            .birthday(LocalDate.of(2001, 1, 1))
            .email("userTest@yandex.ru")
            .login("UserTest1")
            .id(1)
            .build();

    private String json;
    @BeforeEach
    void filmBuilder() throws Exception {
        mockMvc.perform(delete(url+"/all"));        // Чтобы тесты не засорялись
        filmBuilder = Film.builder()
                .name("JunitName")
                .description("JunitDescription")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(90);
    }

    @Test
    void createStandardFilm() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(json))
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
                .andExpect(content().string(containsString("Name cannot be empty")));

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
                .andExpect(content().string(containsString("Max 200 letters")));
    }

    @Test
    void createDateReleaseBeforeThan1895Film() throws Exception {
        LocalDate past = LocalDate.of(1890, 1, 1);
        film = filmBuilder.id(1).releaseDate(past).build();
        json = mapper.writeValueAsString(film);

        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Release date no earlier than December 28, 1895")));
    }

    @Test
    void createNegativeDurationFilm() throws Exception {
        film = filmBuilder.id(1).duration(-10).build();
        json = mapper.writeValueAsString(film);

        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Only positive duration")));
    }

    @Test
    void updateStandardFilm() throws Exception {
        film = filmBuilder.name("Filmname1").id(1).build();
        json = mapper.writeValueAsString(film);

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(content().string(json))
                .andExpect(status().isCreated());

        film = filmBuilder.id(1).name("JunitUpdateName").build();
        json = mapper.writeValueAsString(film);

        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(content().string(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateEmptyFilm() throws Exception {
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(""))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Required request body is missing")));
    }


    @Test
    void standardget() throws Exception {
        film = filmBuilder.name("Film name1").id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                        .andReturn();
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Film name1")));
    }

    @Test
    void emptyget() throws Exception {

        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void likeStandart() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void likeCreateStandart() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser));

        mockMvc.perform(put(url+"/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void likeCreateTwice() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();
        jsonUser = mapper.writeValueAsString(User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest2@yandex.ru")
                .login("UserTest2")
                .id(2)
                .build());
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser));

        mockMvc.perform(put(url+"/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
        mockMvc.perform(put(url+"/1/like/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    void likeCreateNegativeID() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/-1/like/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void likeCreateNonexistentID() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/5231/like/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void likeCreateWrongId() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/1/like/s"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An unexpected error has occurred.")));
    }

    @Test
    void likeDeleteStandart() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(delete(url+"/1/like/1"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void likeDeleteTwice() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        jsonUser = mapper.writeValueAsString(User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest2@yandex.ru")
                .login("UserTest2")
                .id(2)
                .build());
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(delete(url+"/1/like/1"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.size()", is(0)));
        mockMvc.perform(delete(url+"/1/like/2"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void likeDeleteNegativeId() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(delete(url+"/-1/like/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }
    @Test
    void likeDeleteNonexistentID() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(delete(url+"/5231/like/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }
    @Test
    void likeDeleteWrongId() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(delete(url+"/1/like/s"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An unexpected error has occurred.")));
    }

    @Test
    void getTopLikeStandart() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        film = filmBuilder.id(2).name("SecondFilmOne").description("JustLikeFirstButSecond").build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();
        jsonUser = mapper.writeValueAsString(User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest2@yandex.ru")
                .login("UserTest2")
                .id(2)
                .build());
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/2/like/1"));
        mockMvc.perform(put(url+"/2/like/2"));
        mockMvc.perform(put(url+"/1/like/1"));

        mockMvc.perform(get(url+"/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name",is("SecondFilmOne")))
                .andExpect(jsonPath("$.size()",is(2)));
    }

    @Test
    void getLikeCount1() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        film = filmBuilder.id(2).name("SecondFilmOne").description("JustLikeFirstButSecond").build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users?count=1").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();
        jsonUser = mapper.writeValueAsString(User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest2@yandex.ru")
                .login("UserTest2")
                .id(2)
                .build());
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/2/like/1"));
        mockMvc.perform(put(url+"/2/like/2"));
        mockMvc.perform(put(url+"/1/like/1"));
        mockMvc.perform(get(url+"/popular?count=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name",is("SecondFilmOne")))
                .andExpect(jsonPath("$.size()",is(1)));
    }

    @Test
    void getLikeNegativeCount() throws Exception {
        film = filmBuilder.id(1).build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        film = filmBuilder.id(2).name("SecondFilmOne").description("JustLikeFirstButSecond").build();
        json = mapper.writeValueAsString(film);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andReturn();
        String jsonUser = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users?count=1").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();
        jsonUser = mapper.writeValueAsString(User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest2@yandex.ru")
                .login("UserTest2")
                .id(2)
                .build());
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonUser))
                .andReturn();

        mockMvc.perform(put(url+"/2/like/1"));
        mockMvc.perform(put(url+"/2/like/2"));
        mockMvc.perform(put(url+"/1/like/1"));
        mockMvc.perform(get(url+"/popular?count=-2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Count must be positive")));
    }

}