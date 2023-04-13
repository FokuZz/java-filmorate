package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@ToString(includeFieldNames = true)
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class User {
    @Setter
    Integer id;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "This is not Email format")
    @Setter
    String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "\\S+", message = "Login cannot have whitespace")
    @Setter
    String login;
    @Setter
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
}
