package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The date of birth cannot be in the future")
public class BirthdayInFutureException extends RuntimeException{
    public BirthdayInFutureException () {
        super("The date of birth cannot be in the future");
    }
}
