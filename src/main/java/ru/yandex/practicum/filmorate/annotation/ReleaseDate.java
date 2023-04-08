package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface ReleaseDate {

    String message() default "Invalid release date format or value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String format() default "yyyy-MM-dd";
}