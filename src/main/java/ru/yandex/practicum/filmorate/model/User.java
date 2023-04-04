package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.lang.Nullable;


import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@ToString(includeFieldNames=true)
@Builder
public class User {
    @Setter
    private  Integer id;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "This is not Email format")
    private String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "\\S+", message = "Login cannot have whitespace")
    @Setter
    private String login;
    @Setter
    private String name;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @Override
    public boolean equals(Object o){
        if(o == this)return true;
        if(!(o instanceof User)) return false;
        User u = (User) o;
        return  this.login.equals(u.getLogin())
                && this.birthday.equals(u.getBirthday())
                && this.email.equals(u.getEmail());
    }

    @Override
    public int hashCode() {
        return login.hashCode()+birthday.hashCode()+email.hashCode();
    }
}
