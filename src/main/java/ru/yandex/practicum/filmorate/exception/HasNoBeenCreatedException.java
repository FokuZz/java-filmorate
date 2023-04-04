package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The object has no been found to update")
public class HasNoBeenCreatedException extends RuntimeException {
    public HasNoBeenCreatedException() {
        super("The object has no been created");
    }
}
