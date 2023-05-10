package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString(includeFieldNames = true)
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class User {
    @Setter
    Integer id;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "This is not Email format")
    @Size(max = 34, message = "Email has max 34 symbols")
    @Setter
    String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "\\S+", message = "Login cannot have whitespace")
    @Size(max = 24, message = "Login has max 34 symbols")
    @Setter
    String login;
    @Setter
    @Size(max = 24, message = "Name has max 34 symbols")
    String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "The date of birth cannot be in the future")
    @NotNull(message = "Birthday cannot be empty")
    LocalDate birthday;

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        User u = (User) o;
        return this.login.equals(u.getLogin())
                || this.email.equals(u.getEmail());
    }

    @Override
    public int hashCode() {
        return login.hashCode() + email.hashCode();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("login", login);
        map.put("name", name);
        map.put("birthday", birthday);
        return map;
    }
}
