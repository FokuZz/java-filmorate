package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface ReleaseDate {

    String message() default "Release date no earlier than December 28, 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String format() default "yyyy-MM-dd";
}