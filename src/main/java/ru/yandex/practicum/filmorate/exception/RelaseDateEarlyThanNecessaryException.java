package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Release date — no earlier than December 28, 1895")
public class RelaseDateEarlyThanNecessaryException extends RuntimeException {
    public RelaseDateEarlyThanNecessaryException() {
        super("Release date — no earlier than December 28, 1895");
    }
}
