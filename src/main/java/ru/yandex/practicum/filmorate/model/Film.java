package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@ToString(includeFieldNames = true)
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class Film {
    @Setter
    Integer id;

    final Set<Genre> genres = new HashSet<>();
    @Size(max = 24, message = "Name has max 24 symbols")
    @NotBlank(message = "Name cannot be empty")
    String name;
    @ReleaseDate
    LocalDate releaseDate;

    @Positive(message = "Only positive duration")
    @NotNull
    Integer duration;
    @Size(max = 200, message = "Description has max 200 symbols")
    @NotNull
    String description;
    Integer rate;
    Mpa mpa;

    public List<Genre> getGenres() {
        return genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Film)) return false;
        Film f = (Film) o;
        return this.name.equals(f.getName())
                || this.description.equals(f.getDescription());
    }

    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("release_date", releaseDate);
        map.put("duration", duration);
        if (mpa != null) {
            map.put("rating_id", mpa.getId());
        }
        return map;
    }
}
