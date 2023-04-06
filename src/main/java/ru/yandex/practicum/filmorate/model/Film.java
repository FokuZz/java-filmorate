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
@EqualsAndHashCode
@Builder
public class Film {
    @Setter
    private Integer id;
    @NotBlank(message = "Name cannot be empty")
    @NotNull(message = "Name cannot be empty")
    private String name;
    @Size(max = 200, message = "Max 200 letters")
    @NotNull
    private String description;
    @ReleaseDate(message = "Release date — no earlier than December 28, 1895")
    @NotNull
    private LocalDate releaseDate;

    @Positive(message = "Only positive duration")
    @NotNull
    private Integer duration;

}
