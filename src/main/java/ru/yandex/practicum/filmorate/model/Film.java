package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString(includeFieldNames = true)
@EqualsAndHashCode
@Builder
public class Film {
    @Setter
    private Integer id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Size(max = 200, message = "Max 200 letters")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @PositiveOrZero(message = "Only positive duration")
    private Integer duration;
}
