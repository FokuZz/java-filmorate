package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@ToString(includeFieldNames = true)
@Builder
public class Film {
    @Setter
    private Integer id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Size(max = 200, message = "Max 200 letters")
    @NotNull
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Only positive duration")
    @NotNull
    private Integer duration;

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        User u = (User) o;
        return this.name.equals(u.getLogin())
                || this.description.equals(u.getEmail());
    }

    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }
}
