package ru.yandex.practicum.filmorate.validator;


import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

    private DateTimeFormatter formatter;

    @Override
    public void initialize(ReleaseDate annotation) {
        formatter = DateTimeFormatter.ofPattern(annotation.format());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.isAfter(minReleaseDate);
    }
}
