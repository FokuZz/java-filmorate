package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;


@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Data
@Builder
public class Friend {
    @Id
    private Integer id;
    private Integer userId;
    private Integer friendId;
    @NotNull
    private Boolean confirmedFriend;
}
