package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc()
class gitUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    private User.UserBuilder userBuilder;
    private User user;
    private String json;

    private String url = "/users";


    @BeforeEach
    void userBuilder() throws Exception {
        mockMvc.perform(delete(url+"/all"));        // Чтобы тесты не засорялись
        userBuilder = User.builder()
                .birthday(LocalDate.of(2001, 1, 1))
                .email("userTest@yandex.ru")
                .login("UserTest1");
    }

    @Test
    void createStandartUser() throws Exception {
        user = userBuilder.id(1).name("TestName").build();
        json = mapper.writeValueAsString(user);


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
        User userExpected = userBuilder.id(1).name("UserTest1").build();
        String jsonExcepted = mapper.writeValueAsString(userExpected);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(jsonExcepted));
    }


    @Test
    void createEmptyLoginUser() throws Exception {
        user = userBuilder.id(1).login(null).build();
        json = mapper.writeValueAsString(user);

        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Login cannot be empty")));
    }

    @Test
    void createEmptyEmailUser() throws Exception {
        user = userBuilder.id(1).email(null).build();
        json = mapper.writeValueAsString(user);

        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email cannot be empty")));
    }

    @Test
    void createFutureBirthDayUser() throws Exception {
        user = userBuilder.id(1).birthday(LocalDate.of(2025, 1, 1)).build();
        json = mapper.writeValueAsString(user);

        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("The date of birth cannot be in the future")));
    }


    @Test
    void updateStandartUser() throws Exception {
        user = userBuilder.name("FirstName").id(1).build();
        json = mapper.writeValueAsString(user);

        mockMvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(json))
                .andDo(print())
                .andExpect(content().string(json))
                .andExpect(status().isCreated());

        user = userBuilder.id(1).name("TestName").build();
        json = mapper.writeValueAsString(user);

        mockMvc.perform(put(url).contentType(APPLICATION_JSON_UTF8).content(json))
                .andDo(print())
                .andExpect(content().string(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateEmptyUser() throws Exception {
        user = userBuilder.name("FirstName").id(1).build();

        mockMvc.perform(put(url).contentType(APPLICATION_JSON_UTF8).content(""))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Required request body is missing")));
    }

    @Test
    void getStandartFilms() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

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

    //TODO дописать тесты и быть крутым
    @Test
    void createFriendStandart() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(put(url+"/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(1)))
                .andExpect(jsonPath("$[0].login",is("LoginTest2")));
    }

    @Test
    void createFriendTwice() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest3").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url+"/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(1)))
                .andExpect(jsonPath("$[0].login",is("LoginTest2")));
        mockMvc.perform(put(url+"/1/friends/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(2)))
                .andExpect(jsonPath("$[1].login",is("LoginTest3")));
    }

    @Test
    void createFriendWrongId() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(put(url+"/1/friends/41"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void createFriendNegativeId() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(put(url+"/1/friends/-41"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void deleteFriendStandart() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url+"/1/friends/2"));

        mockMvc.perform(delete(url+"/1/friends/2"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.size()",is(0)));
    }

    @Test
    void deleteFriendTwice() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest3").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url+"/1/friends/2"));
        mockMvc.perform(put(url+"/1/friends/3"));

        mockMvc.perform(delete(url+"/1/friends/2"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.size()",is(1)));
        mockMvc.perform(delete(url+"/1/friends/3"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.size()",is(0)));
    }

    @Test
    void deleteFriendWrong() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url+"/1/friends/2"));

        mockMvc.perform(delete(url+"/1/friends/21"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void deleteFriendNegative() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url+"/1/friends/2"));

        mockMvc.perform(delete(url+"/1/friends/-21"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void getFriendStandart() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url+"/1/friends/2"));

        mockMvc.perform(get(url+"/1/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(1)))
                .andExpect(jsonPath("$[0].login",is("LoginTest2")));
    }

    @Test
    void getFriendTwice() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest3").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        mockMvc.perform(put(url + "/1/friends/2"));
        mockMvc.perform(put(url + "/1/friends/3"));

        mockMvc.perform(get(url + "/1/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].login", is("LoginTest2")))
                .andExpect(jsonPath("$[1].login", is("LoginTest3")));
    }

    @Test
    void getFriendEmpty() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(get(url + "/1/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void getFriendWrong() throws Exception {
        user = userBuilder.name("UserName1").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(get(url + "/23/friends"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }

    @Test
    void getCommonFriendsStandart() throws  Exception {
        user = userBuilder.login("LoginTest1").email("EmailTest1@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest3").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest4").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest5").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(put(url + "/5/friends/2"));
        mockMvc.perform(put(url + "/5/friends/3"));
        mockMvc.perform(put(url + "/1/friends/4"));
        mockMvc.perform(put(url + "/1/friends/5"));
        mockMvc.perform(put(url + "/1/friends/2"));

        mockMvc.perform(get(url + "/1/friends/common/5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(1)))
                .andExpect(jsonPath("$[0].login",is("LoginTest2")));

    }

    @Test
    void getCommonFriendsTwice() throws  Exception {
        user = userBuilder.login("LoginTest1").email("EmailTest1@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest3").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest4").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest5").email("EmailTest3@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(put(url + "/5/friends/2"));
        mockMvc.perform(put(url + "/5/friends/3"));
        mockMvc.perform(put(url + "/1/friends/4"));
        mockMvc.perform(put(url + "/1/friends/3"));
        mockMvc.perform(put(url + "/1/friends/5"));
        mockMvc.perform(put(url + "/1/friends/2"));

        mockMvc.perform(get(url + "/1/friends/common/5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(2)))
                .andExpect(jsonPath("$[0].login",is("LoginTest2")))
                .andExpect(jsonPath("$[1].login",is("LoginTest3")));

    }

    @Test
    void getCommonFriendsEmpty() throws  Exception {
        user = userBuilder.login("LoginTest1").email("EmailTest1@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(get(url + "/1/friends/common/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(0)));
    }

    @Test
    void getCommonFriendsWrong() throws  Exception {
        user = userBuilder.login("LoginTest1").email("EmailTest1@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));
        user = userBuilder.login("LoginTest2").email("EmailTest2@mail.ru").id(1).build();
        json = mapper.writeValueAsString(user);
        mockMvc.perform(post(url).content(json).contentType(APPLICATION_JSON_UTF8));

        mockMvc.perform(get(url + "/1/friends/common/25"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The object has no been Found")));
    }
}