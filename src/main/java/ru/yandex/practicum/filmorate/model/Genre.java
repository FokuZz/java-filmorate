package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Size;

@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class Genre {
    @Id
    private Integer id;
    @Size(max = 32, message = "Name has max 34 symbols")
    private String name;
}
